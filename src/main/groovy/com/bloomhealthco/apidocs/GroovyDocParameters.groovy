package com.bloomhealthco.apidocs

/**
 * User: rgahagan
 * Date: 1/10/13
 * Time: 4:52 PM
 */
class GroovyDocParameters {
    File output
    String docBasePath
    String apiBasePath
    String apiVersion

    public static GroovyDocParameters parse(String[][] options) {
        GroovyDocParameters parameters = new GroovyDocParameters()

        for (String[] option : options) {
            if (option[0].equals("-d")) {
                parameters.output = new File(option[1])
            } else if(option[0].equals("-docBasePath")) {
                parameters.docBasePath = option[1]
            } else if(option[0].equals("-apiBasePath")) {
                parameters.apiBasePath = option[1]
            } else if(option[0].equals("-apiVersion")) {
                parameters.apiVersion = option[1]
            }
        }

        return parameters;
    }
}
