package org.sortdc.sortdc.resources;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.sortdc.sortdc.Classifier;
import org.sortdc.sortdc.Log;
import org.sortdc.sortdc.dao.Category;
import org.sortdc.sortdc.database.ObjectNotFoundException;
import org.sortdc.sortdc.resources.dto.CategoriesDTO;
import org.sortdc.sortdc.resources.dto.CategoryDTO;

@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
public class CategoriesResource {

    private Classifier classifier;

    public CategoriesResource(Classifier classifier) {
        this.classifier = classifier;
    }

    /**
     * Retrieves list of all classifier's categories
     * 
     * @return
     */
    @GET
    public CategoriesDTO get() {
        CategoriesDTO categories_dto = new CategoriesDTO(this.classifier.getId());
        for (String category_id : this.classifier.getCategories().keySet()) {
            CategoryDTO category_dto = new CategoryDTO(this.classifier.getId(), category_id);
            categories_dto.add(category_dto);
        }
        return categories_dto;
    }

    /**
     * Deletes all classifier's categories and documents
     * 
     * @return
     */
    @DELETE
    public Response delete() {
        try {
            this.classifier.empty();
        } catch (Exception e) {
            Log.getInstance().add(e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
        return Response.ok().build();
    }

    /**
     * Returns a new category resource
     * 
     * @param category_id
     * @return
     */
    @Path("/{category: [a-zA-Z0-9_-]+}")
    public CategoryResource getCategoryResource(@PathParam("category") String category_id) {
        Category category;
        try {
            category = this.classifier.getCategory(category_id);
        } catch (ObjectNotFoundException e) {
            category = new Category();
            category.setId(category_id);
        }
        return new CategoryResource(this.classifier, category);
    }
}
