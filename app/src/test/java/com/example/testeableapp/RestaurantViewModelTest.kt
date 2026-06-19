package com.example.testeableapp

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RestaurantViewModelTest {

    private lateinit var viewModel: RestaurantViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        viewModel = RestaurantViewModel()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // PRUEBAS

    @Test
    fun `agregar item al pedido aumenta la cantidad`() {
        viewModel.addItem(1)

        val quantities = viewModel.quantities.value
        assertEquals(1, quantities[1])
    }

    @Test
    fun `incrementar y decrementar cantidad actualiza el mapa correctamente`() {
        viewModel.addItem(1)
        viewModel.incrementItem(1)
        assertEquals(2, viewModel.quantities.value[1])

        viewModel.decrementItem(1)
        assertEquals(1, viewModel.quantities.value[1])
    }

    @Test
    fun `eliminar item al decrementar desde uno remueve el item del mapa`() {
        viewModel.addItem(2)
        viewModel.decrementItem(2)

        assertFalse(viewModel.quantities.value.containsKey(2))
    }

    @Test
    fun `calculo del total a pagar suma correctamente los precios`() = runTest {
        val job = launch(UnconfinedTestDispatcher()) { viewModel.total.collect() }

        viewModel.addItem(1)
        viewModel.addItem(5)
        viewModel.incrementItem(5)

        assertEquals(8.50, viewModel.total.value, 0.01)

        job.cancel()
    }

    // PRUEBAS EXTRA

    @Test
    fun `realizar pedido con items actualiza confirmacion`() = runTest {
        val job1 = launch(UnconfinedTestDispatcher()) { viewModel.orderedItems.collect() }
        val job2 = launch(UnconfinedTestDispatcher()) { viewModel.total.collect() }

        viewModel.addItem(1) // 1 item
        viewModel.addItem(2) // 1 item

        viewModel.placeOrder()

        val confirmation = viewModel.confirmation.value
        assertTrue(confirmation != null)
        assertEquals(2, confirmation?.itemCount)

        job1.cancel()
        job2.cancel()
    }

    @Test
    fun `descartar confirmacion limpia el pedido`() {
        viewModel.addItem(1)
        viewModel.placeOrder()

        viewModel.dismissConfirmation()

        assertNull(viewModel.confirmation.value)
        assertTrue(viewModel.quantities.value.isEmpty())
    }
}