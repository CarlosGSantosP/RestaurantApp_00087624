package com.example.testeableapp

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import com.example.testeableapp.model.MenuData
import org.junit.Rule
import org.junit.Test

class RestaurantAppUITest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun setupApp() {
        composeTestRule.setContent {
            RestaurantOrderApp(viewModel = RestaurantViewModel())
        }
    }

    // PRUEBAS

    @Test
    fun muestraMensajeDePedidoVacioAlInicio() {
        setupApp()

        composeTestRule.onNodeWithTag("emptyOrderMessage")
            //.performScrollTo()
            .assertIsDisplayed()
    }

    @Test
    fun todosLosItemsDelMenuEstanVisibles() {
        setupApp()

        MenuData.items.forEach { item ->
            composeTestRule.onNodeWithTag("menuItem_${item.id}")
                .performScrollTo()
                .assertIsDisplayed()
        }
    }

    @Test
    fun elTotalGeneralSeActualizaAlAgregarItems() {
        setupApp()

        composeTestRule.onNodeWithTag("addButton_1").performClick()

        composeTestRule.onNodeWithTag("totalValue")
            .assertTextEquals("5.50 €")

        composeTestRule.onNodeWithTag("addButton_5").performClick()

        composeTestRule.onNodeWithTag("totalValue")
            .assertTextEquals("7.00 €")
    }

    // PRUEBAS EXTRA
    @Test
    fun realizarPedidoMuestraDialogoDeConfirmacion() {
        setupApp()

        composeTestRule.onNodeWithTag("addButton_1").performClick()

        composeTestRule.onNodeWithTag("placeOrderButton")
            .performScrollTo()
            .performClick()

        composeTestRule.onNodeWithTag("confirmationDialog")
            .assertIsDisplayed()
    }

    @Test
    fun aceptarDialogoDeConfirmacionLoOcultaYVaciaElPedido() {
        setupApp()

        composeTestRule.onNodeWithTag("addButton_1").performClick()

        composeTestRule.onNodeWithTag("placeOrderButton")
            .performScrollTo()
            .performClick()

        composeTestRule.onNodeWithTag("confirmationOkButton")
            .performClick()

        composeTestRule.onNodeWithTag("confirmationDialog")
            .assertDoesNotExist()

        composeTestRule.onNodeWithTag("emptyOrderMessage")
            .performScrollTo()
            .assertIsDisplayed()
    }
}
