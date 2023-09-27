package com.fulfillmenttools.danieltestapplication

/**
 * Created on 12.09.2023. Copyright by OC fulfillment GmbH.
 */
class CPCLRepository {

    fun getLabel(): String = """
                ! 0 200 200 210 1
                TEXT 4 0 30 40 Hallo Mark,
                TEXT 4 0 30 80 du suesser Schmakkofatz!
                TEXT 4 0 30 120      Daniel <3
                FORM
                PRINT
                """.trimIndent()
}
