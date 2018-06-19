yieldUnescaped '<!DOCTYPE html>'                                                    
html(lang:'en') {                                                                   
    head {                                                                          
        meta('http-equiv':'"Content-Type" content="text/html; charset=utf-8"')
        link(rel: "stylesheet", href: "${request.contextPath}/ozark.css")    
        title('Hello')                                                            
    }                                                                               
    body {                                                                          
        h1("Hello $user")                                    
    }                                                                               
}  