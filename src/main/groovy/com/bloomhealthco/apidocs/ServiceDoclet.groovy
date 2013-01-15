package com.bloomhealthco.apidocs

import com.sun.javadoc.DocErrorReporter
import com.sun.javadoc.LanguageVersion
import org.codehaus.groovy.tools.groovydoc.SimpleGroovyMethodDoc
import org.codehaus.jackson.map.ObjectMapper
import org.codehaus.jackson.map.SerializationConfig
import org.codehaus.groovy.groovydoc.*
import com.sun.javadoc.RootDoc
import org.codehaus.groovy.enhancedgroovydoc.wrapper.RootDocWrapper

/**
 * User: rgahagan
 * Date: 1/10/13
 * Time: 4:47 PM
 */
class ServiceDoclet {
    static String PATH = "@Path"
    static String PATH_PARAM = "PathParam"
    static String QUERY_PARAM = "QueryParam"

    static String docBasePath = "http://localhost:8080"
    static String apiBasePath = "http://localhost:8080"
    static String apiVersion = "0"

    static List<String> METHODS = new ArrayList<String>() {{
        add("@GET")
        add("@PUT")
        add("@POST")
        add("@DELETE")
    }}

    static boolean start(GroovyRootDoc doc) {
        RootDoc unwrapped = new RootDocWrapper(doc)

        GroovyDocParameters parameters = GroovyDocParameters.parse(unwrapped.options())

        if(parameters.getDocBasePath()!=null)
            docBasePath=parameters.getDocBasePath()
        if(parameters.getApiBasePath()!=null)
            apiBasePath=parameters.getApiBasePath()
        if(parameters.getApiVersion()!=null)
            apiVersion=parameters.getApiVersion()

        Map<String, Map<String,List<Method>>> apiMap = new HashMap<String, Map<String,List<Method>>>()

        ObjectMapper mapper = new ObjectMapper()
        mapper.configure(SerializationConfig.Feature.INDENT_OUTPUT, true)

        try {
            List<ResourceListingAPI> builder = new LinkedList<ResourceListingAPI>()
            for (GroovyClassDoc classDoc : doc.classes()) {
                 //go through each class
                String apiPath = path(classDoc.annotations())
                if (apiPath != null) {
                    Map<String,List<Method>> methodMap = apiMap.get(apiPath)
                    if(methodMap==null){
                        methodMap = new HashMap<String,List<Method>>()
                    }

                    //add all jax-rs annotated methods to the methodmap
                    for (GroovyMethodDoc method : classDoc.methods()) {
                        Method me = parseMethod(method)
                        if (me != null) {
                            List<Method> methods = methodMap.get(me.getPath())
                            if (methods==null){
                                methods = new ArrayList<Method>()
                            }
                            methods.add(me)
                            methodMap.put(me.getPath(), methods)

                        }
                    }
                    apiMap.put(apiPath, methodMap)
                }
            }

            //Sort the classes based upon class path annotation
            List<String> apiList = new ArrayList<String>(apiMap.keySet())
            Collections.sort(apiList)

            for(String apiPath: apiList){
                List<Api> apiBuilder = new LinkedList<Api>()

                Map<String,List<Method>> methodMap = apiMap.get(apiPath)
                List<String> keyList = new ArrayList<String>(methodMap.keySet())
                Collections.sort(keyList)
                for(String path:keyList){
                    //turn list of methods into list of api objects
                    List<Operation> methodBuilder = new LinkedList<Operation>()

                    for(Method me:methodMap.get(path)){
                        methodBuilder.add(new Operation(me.getMethod(), me.getMethodName(), typeOf(me.getReturnType()),
                                me.getApiParameters(), me.getFirstSentence(), me.getComment()))
                    }
                    apiBuilder.add(new Api(apiPath+path, "", methodBuilder))
                }

                //write out json for methods
                String rootPath = (apiPath.startsWith("/") ? apiPath.replaceFirst("/", "") : apiPath).replaceAll("/", "_").replaceAll("(\\{|\\})", "")
                builder.add(new ResourceListingAPI("/" + rootPath + ".{format}",""))

                File apiFile = new File(parameters.getOutput(), rootPath + ".json")
                ApiDeclaration declaration = new ApiDeclaration(apiVersion, apiBasePath, apiBuilder)

                mapper.writeValue(apiFile, declaration)
            }

            //write out json for api
            ResourceListing listing = new ResourceListing(apiVersion, docBasePath, builder)
            File docFile = new File(parameters.getOutput(), "service.json")
            mapper.writeValue(docFile, listing)

            return true
        } catch (IOException e) {
            return false
        }
    }

    /**
     * Gets the string representation of the jax-rs path from an array of annotations.
     *
     * @param annotations
     * @return
     */
    private static String path(GroovyAnnotationRef[] annotations) {
        for (GroovyAnnotationRef annotationRef : annotations) {
            if (annotationRef.description().contains(PATH)) {
                String path = annotationRef.description().substring(annotationRef.description().indexOf("\"") + 1, annotationRef.description().lastIndexOf("\""))
                return path.startsWith("/") ? path : "/" + path
            }
        }
        return null
    }

    /**
     * Turns a MethodDoc(Javadoc) into a swagger serialize-able method object.
     *
     * @param method
     * @return
     */
    private static Method parseMethod(SimpleGroovyMethodDoc method) {
        for (GroovyAnnotationRef ref : method.annotations()) {
            if (METHODS.contains(ref.description())) {

                //Path
                String path = path(method.annotations())
                if (path==null) {
                    path = ""
                }

                //Parameters
                List<ApiParameter> parameterBuilder = new LinkedList<ApiParameter>()

                for (GroovyParameter parameter : method.parameters()) {
                    String parameterComment = commentForParameter(method, parameter)
                    parameterBuilder.add(new ApiParameter(
                            paramTypeOf(parameter),
                            parameter.name(),
                            parameterComment,
                            typeOf(parameter.typeName()))
                    )
                }

                //First Sentence of Javadoc method description
                String fss = method.firstSentenceCommentText()

                return new Method(ref.description().substring(1),
                        method.name(),
                        parameterBuilder,
                        fss,
                        "",
                        method.returnType.typeName() == null ? method.returnType.getFullPathName() : method.returnType.typeName(),
                        path
                )
            }
        }
        return null
    }

    /**
     * Gets the string representation of the parameter comment from the Javadoc.
     *
     * @param method
     * @param parameter
     * @return
     */
    private static String commentForParameter(SimpleGroovyMethodDoc method, GroovyParameter parameter) {
        for (GroovyTag tag : method.tags()) {
            if (tag.param().equals(parameter.name())) {
                return tag.text().replaceAll("\n", "")
            }
        }
        return null
    }

    /**
     * Determines the string representation of the parameter type.
     *
     * @param parameter
     * @return
     */
    private static String paramTypeOf(GroovyParameter parameter) {
        for (GroovyAnnotationRef annotation : parameter.annotations()) {
            if (annotation.name().equals(PATH_PARAM)) {
                return "path"
            } else if (annotation.name().equals(QUERY_PARAM)) {
                return "query"
            }
        }
        return "body"
    }

    /**
     * Determines the String representation of the object Type.
     *
     * @param javaType
     * @return
     */
    private static String typeOf(String javaType) {
        if (javaType.equals("String") || javaType.equals("java.lang.String")) {
            return "string"
        } else if(javaType.equals("java.util.Date")) {
            return "Date"
        } else {
            // TODO: have to make sure we add this type to the models section
            int i = javaType.lastIndexOf(".")
            if(i>=0){
                return javaType.substring(i+1)
            } else {
                i = javaType.lastIndexOf("/")
                if (i>0){
                    return javaType.substring(i+1)
                }

            }
            return javaType
        }
    }

    /**
     * Check for doclet-added options.  Returns the number of
     * arguments you must specify on the command line for the
     * given option.  For example, "-d docs" would return 2.
     * <p/>
     * This method is required if the doclet contains any options.
     * If this method is missing, Javadoc will print an invalid flag
     * error for every option.
     *
     * @return number of arguments on the command line for an option
     *         including the option name itself.  Zero return means
     *         option not known.  Negative value means error occurred.
     */
    public static int optionLength(String option) {
        Map<String, Integer> options = new HashMap<String, Integer>()
        options.put("-d", 2)
        options.put("-docBasePath", 2)
        options.put("-apiBasePath", 2)
        options.put("-apiVersion", 2)

        Integer value = options.get(option)
        if (value != null) {
            return value
        } else {
            return 0
        }
    }

    /**
     * Check that options have the correct arguments.
     * <p/>
     * This method is not required, but is recommended,
     * as every option will be considered valid if this method
     * is not present.  It will default gracefully (to true)
     * if absent.
     * <p/>
     * Printing option related error messages (using the provided
     * DocErrorReporter) is the responsibility of this method.
     *
     * @return true if the options are valid.
     */
    public static boolean validOptions(String[][] options, DocErrorReporter reporter) {
        return true
    }

    /**
     * Return the version of the Java Programming Language supported
     * by this doclet.
     * <p/>
     * This method is required by any doclet supporting a language version
     * newer than 1.1.
     *
     * @return the language version supported by this doclet.
     * @since 1.5
     */
    public static LanguageVersion languageVersion() {
        return LanguageVersion.JAVA_1_5
    }
}
