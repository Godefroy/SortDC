package org.sortdc.sortdc.resources;

import com.sun.jersey.api.NotFoundException;
import java.net.URI;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.sortdc.sortdc.Classifier;
import org.sortdc.sortdc.Log;
import org.sortdc.sortdc.dao.Category;
import org.sortdc.sortdc.dao.Document;
import org.sortdc.sortdc.database.ObjectNotFoundException;
import org.sortdc.sortdc.resources.dto.CategoryDTO;
import org.sortdc.sortdc.resources.dto.DocumentDTO;

@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
public class DocumentsResource {

    private Classifier classifier;
    private Category category;

    public DocumentsResource(Classifier classifier) {
        this.classifier = classifier;
    }

    public DocumentsResource(Classifier classifier, Category category) {
        this.classifier = classifier;
        this.category = category;
    }

    /**
     * Retrieves list of all documents
     * 
     * @return
     */
    @GET
    public void get() {
        // TODO
    }

    /**
     * Adds a new document
     * 
     * @return
     */
    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response post(DocumentDTO request) {
        String category_id;
        if (request.category != null && request.category.id != null) {
            if (!request.category.id.matches("^[a-zA-Z0-9_-]+$")) {
                throw new WebApplicationException(Response.Status.BAD_REQUEST);
            }
            category_id = request.category.id;
        } else if (this.category != null) {
            category_id = this.category.getId();
        } else {
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }

        Document document = new Document();
        if (request.id != null) {
            if (!request.id.matches("^[a-zA-Z0-9_-]+$")) {
                throw new WebApplicationException(Response.Status.BAD_REQUEST);
            }
            document.setId(request.id);
        }
        document.setCategoryId(category_id);

        try {
            document.setTokensOccurrences(this.classifier.extractTokens(request.text, request.html, request.getTokens()));
            this.classifier.saveDocument(document);
        } catch (Exception e) {
            Log.getInstance().add(e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }

        DocumentDTO document_dto = this.getDocumentDTO(document);
        URI uri = URI.create(document_dto.getHref());
        return Response.created(uri).entity(document_dto).build();
    }

    /**
     * Adds a new document
     * 
     * @return
     */
    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    public Response postTextPlain(String text) {
        if (this.category == null) {
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }

        Document document = new Document();
        document.setCategoryId(this.category.getId());
        try {
            document.setTokensOccurrences(this.classifier.extractTokens(text, null, null));
            this.classifier.saveDocument(document);
        } catch (Exception e) {
            Log.getInstance().add(e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }

        DocumentDTO document_dto = this.getDocumentDTO(document);
        URI uri = URI.create(document_dto.getHref());
        return Response.created(uri).entity(document_dto).build();
    }

    /**
     * Generates a DocumentDTO from a Document
     * 
     * @param document
     * @return 
     */
    public DocumentDTO getDocumentDTO(Document document) {
        String classifier_id = this.classifier.getId();
        DocumentDTO document_dto = new DocumentDTO(classifier_id, document.getId());
        document_dto.category = new CategoryDTO(classifier_id, document.getCategoryId());
        document_dto.setTokens(document.getTokensOccurrences());
        return document_dto;
    }

    /**
     * Deletes all classifier's categories and documents
     * 
     * @return
     */
    @DELETE
    public void delete() {
        // TODO
    }

    /**
     * Returns a new document resource
     * 
     * @param document_id
     * @return
     */
    @Path("/{document: [a-zA-Z0-9_-]+}")
    public DocumentResource getDocumentResource(@PathParam("document") String document_id) {
        if (this.category != null) {
            throw new NotFoundException();
        }
        Document document;
        try {
            document = this.classifier.findDocumentById(document_id);
        } catch (ObjectNotFoundException e) {
            throw new NotFoundException();
        } catch (Exception e) {
            Log.getInstance().add(e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
        try {
            this.category = this.classifier.getCategory(document.getCategoryId());
        } catch (ObjectNotFoundException e) {
            System.out.println(e.getMessage());
            throw new NotFoundException();
        }
        return new DocumentResource(this.classifier, this.category, document);
    }
}
