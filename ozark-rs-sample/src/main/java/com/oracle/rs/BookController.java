package com.oracle.rs;

import javax.mvc.Controller;
import javax.mvc.Models;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.PathParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

/**
 * BookController sample. The @Controller instructs the JAX-RS runtime to
 * use a service provider to call a method. If @Controller is applied to
 * the class, instead of a method, then all methods in that class are
 * invoked via the service provider.
 *
 * Details as to how a service provider is registered, and connected to
 * an annotation such as @Controller, are TBD.
 *
 * @author Santiago Pericas-Geertsen
 */
@Path("/book/{id}")
public class BookController {

    /**
     * Application class used to find books.
     */
    @Inject
    private Catalog catalog;

    /**
     * MVC Framework class used to bind models by name.
     */
    @Inject
    private Models models;

    /**
     * MVC controller to render a book in HTML.
     *
     * @param id ID of the book given in URI.
     * @return JSP page used for rendering.
     */
    @GET
    @Controller         // Process method via service provider
    @Produces("text/html")
    public String html(@PathParam("id") String id) {
        models.set("book", catalog.getBook(id));
        return "book.jsp";      // JSP to render a book
    }

    /**
     * REST method that returns a JSON representation. Not processed via the
     * service provider since it is not annotated by @Controller.
     *
     * @param id ID of the book given in URI.
     * @return Book model instance.
     */
    @GET
    @Produces("text/json")
    public Book json(@PathParam("id") String id) {
        return catalog.getBook(id);
    }
}
