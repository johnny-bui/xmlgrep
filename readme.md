xmlgrep
========

install
-------

copy dist/xmlgrep.jar somewhere in your hdd.

run
----

    java -jar [path/to/]xmlgrep.jar <filename> <xpath-string>

E.g

    java -jar dist/xmlgrep build.xml //project/import
    
    query: //project/import
    #document|project|import 12
    Found 1 node(s) in build.xml
