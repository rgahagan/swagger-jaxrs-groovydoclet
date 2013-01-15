package com.bloomhealthco.apidocs

/**
 * User: rgahagan
 * Date: 1/11/13
 * Time: 5:28 PM
 */
class Operation {
    String httpMethod
    String nickname
    String responseClass // void, primitive, complex or a container
    List<ApiParameter> parameters
    String summary // cap at 60 characters for readability in the UI
    String notes

    Operation(String httpMethod, String nickname, String responseClass, List<ApiParameter> parameters, String summary, String notes) {
        this.httpMethod = httpMethod
        this.nickname = nickname
        this.responseClass = responseClass
        this.parameters = parameters
        this.summary = summary
        this.notes = notes
    }
}
