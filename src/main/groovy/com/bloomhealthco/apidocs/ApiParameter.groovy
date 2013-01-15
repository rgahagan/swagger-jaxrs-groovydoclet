package com.bloomhealthco.apidocs

/**
 * User: rgahagan
 * Date: 1/10/13
 * Time: 4:58 PM
 */
class ApiParameter {
    String paramType
    String name
    String description
    String dataType

    ApiParameter(String paramType, String name, String description, String dataType) {
        this.paramType = paramType
        this.name = name
        this.description = description
        this.dataType = dataType
    }

    public boolean getRequired() {
        return !paramType.equals("query")
    }

    public AllowableValues getAllowableValues() {
        if(dataType.equals("boolean")){
            List<String> values = new ArrayList<String>()
            values.add("false")
            values.add("true")
            return new AllowableValues(values)
        } else {
            return null
        }

    }
}
