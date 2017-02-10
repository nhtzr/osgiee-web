# osgiee-web

Trying to deploy a spring 4 app to karaf.

```
feature:repo-add cxf 3.1.11-SNAPSHOT
feature:install spring/4.2.8.RELEASE_1 hibernate/4.3.6.Final jpa/2.3.0 cxf-jaxrs http http-whiteboard
bundle:install -s mvn:org.eclipse.gemini.blueprint/gemini-blueprint-io/2.0.0.RELEASE mvn:org.eclipse.gemini.blueprint/gemini-blueprint-core/2.0.0.RELEASE mvn:org.eclipse.gemini.blueprint/gemini-blueprint-extender/2.0.0.RELEASE
bundle:install -s mvn:org.codehaus.jackson/jackson-jaxrs/1.9.13 mvn:org.codehaus.jackson/jackson-core-asl/1.9.13 mvn:org.codehaus.jackson/jackson-mapper-asl/1.9.13 mvn:org.codehaus.jackson/jackson-xc/1.9.7
bundle:install -s mvn:mx.nhtzr/osgiee-web
```
