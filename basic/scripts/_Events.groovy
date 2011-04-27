eventAllTestsStart = {
    if (getBinding().variables.containsKey("functionalTests")) {
        functionalTests << "functional"
    }
}
