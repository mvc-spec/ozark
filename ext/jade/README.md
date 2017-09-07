# Jade View Engine

A Ozark Extension for the [Jade Template Engine][jade] using [Jade4J][jade4j].

The extension can be configured via SystemProperties or a configuration file named "jade.properties" in the classpath (SystemProperties win).
The default values are:

    org.mvcspec.ozark.ext.jade.mode=XHML
    org.mvcspec.ozark.ext.jade.caching=true
    org.mvcspec.ozark.ext.jade.prettyPrint=false
    org.mvcspec.ozark.ext.jade.encoding=UTF-8
    
[Filters][filters] and [Helpers][helpers] can be registered with their fully qualified class name:

    org.mvcspec.ozark.ext.jade.filters.myFilter=com.foo.bar.MyFilter
    org.mvcspec.ozark.ext.jade.helpers.myHelper=com.foo.bar.MyHelper
    
This extension does not register any Filter or Helper. Jade4j registers 3 filters per default:

    css=de.neuland.jade4j.filter.CssFilter
    js=de.neuland.jade4j.filter.JsFilter
    cdata=de.neuland.jade4j.filter.CDATAFilter

## TODOs

* The combination of filters and includes does not seem to work. See [jade4j #100][100].


 [jade]: http://jade-lang.com/
 [jade4j]: https://github.com/neuland/jade4j
 [filters]: https://github.com/neuland/jade4j#api-filters
 [helpers]: https://github.com/neuland/jade4j#api-helpers
 [100]: https://github.com/neuland/jade4j/issues/100
