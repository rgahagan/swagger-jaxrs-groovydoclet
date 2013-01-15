package com.bloomhealthco.apidocs

/**
 * User: rgahagan
 * Date: 1/11/13
 * Time: 5:29 PM
 */
class ApiDeclaration {
    String apiVersion
    String basePath
    List<Api> apis
    String swaggerVersion = "1.1"

    ApiDeclaration(String apiVersion, String basePath, List<Api> apis) {
        this.apiVersion = apiVersion
        this.basePath = basePath
        this.apis = apis
    }
}
