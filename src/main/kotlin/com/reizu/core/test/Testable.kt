package com.reizu.core.test

/**
 * Interface specifying methods required for tests
 */
interface Testable {

    /**
     * Method to prepare data before test class
     */
    fun preSetUp()

    /**
     * Method to prepare data before test
     */
    fun setUp()

    /**
     * Method to remove data before test
     */
    fun tearDown()

    /**
     * Method to remove data after test class
     */
    fun postTearDown()

}
