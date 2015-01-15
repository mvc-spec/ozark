
Ozark Samples
-------------

 All samples can be compiled using JDK 8 and deployed on GlassFish 4.1 or later. 

 o book-cdi: Shows how to bind view template parameters using CDI. An instance of Book in
   request scope is injected in BookController and also accessed directly by the JSP.

 o book-models: Shows how to bind view template parameters using the Models interfaces from 
   the MVC API. The Models interface allows binding of parameters by name.

 o conversation: An example on how to use the conversation scope from CDI. Note in particular
   the use of the 'cdi' query parameter to access a specific conversation. This is required 
   for any servlet-based application.


