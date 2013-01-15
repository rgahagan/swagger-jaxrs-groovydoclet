package com.bloomhealthco.apidocs

/**
 * User: rgahagan
 * Date: 1/11/13
 * Time: 5:27 PM
 */
class Api {
    String path
    String description
    List<Operation> operations

    Api(String path, String description, List<Operation> operations) {
        this.path = path
        this.description = description
        this.operations = operations
    }
}
