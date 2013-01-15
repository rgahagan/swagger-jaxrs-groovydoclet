package com.bloomhealthco.apidocs

/**
 * User: rgahagan
 * Date: 1/11/13
 * Time: 5:30 PM
 */
class ResourceListing {
    String apiVersion
    String basePath
    List<ResourceListingAPI> apis
    String swaggerVersion = "1.1"

    ResourceListing(String apiVersion, String basePath, List<ResourceListingAPI> apis) {
        this.apiVersion = apiVersion
        this.basePath = basePath
        this.apis = apis
    }
}
