package com.bloomhealthco.apidocs

/**
 * User: rgahagan
 * Date: 1/10/13
 * Time: 4:57 PM
 */
class Method {
    String method
    String methodName
    List<ApiParameter> apiParameters
    String firstSentence
    String comment
    String returnType
    String path

    Method(String method, String methodName, List<ApiParameter> apiParameters, String firstSentence, String comment, String returnType, String path) {
        this.method = method
        this.methodName = methodName
        this.apiParameters = apiParameters
        this.firstSentence = firstSentence
        this.comment = comment
        this.returnType = returnType
        this.path = path
    }
}
