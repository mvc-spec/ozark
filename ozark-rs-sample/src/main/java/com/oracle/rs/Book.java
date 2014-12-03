package com.oracle.rs;

/**
 * Interface Book.
 *
 * @author Santiago Pericas-Geertsen
 */
public interface Book {

    String getTitle();
    void setTitle(String title);

    String getAuthor();
    void setAuthor(String author);

    String getIsbn();
    void setIsbn(String isbn);
}
