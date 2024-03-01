package es.iessoterhernandez.daw.endes.Ej3._CuentaTarjeta;


import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.is;

import java.util.Date;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class TestCredito {

    Credito credito;
    final String titular = "Antonio", COMERCIO = "Mercadona";

    @BeforeEach
    void init() {
        credito = new Credito("1", titular, new Date(2024, 1, 1), 200D);
        credito.setCuenta(new Cuenta("123456789", titular));
    }

    @AfterEach
    void finish() {
        credito = null;
    }

    @ParameterizedTest(name = "Ingresar {0}€")
    @MethodSource("cantidades")
    void testIngresarCredito(double cantidad) {
        try {
            credito.ingresar(cantidad);
            assertEquals(credito.mCuentaAsociada.mMovimientos.size(), credito.mMovimientos.size());
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    @ParameterizedTest(name = "Retirar {0}€")
    @MethodSource("cantidades")
    void testRetirarCredito(double cantidad) {
        try {
            credito.retirar(cantidad);
            assertNotEquals(credito.mCuentaAsociada.mMovimientos.size(), credito.mMovimientos.size());
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    @ParameterizedTest(name = "Pagar {0}€ en " + COMERCIO)
    @MethodSource("cantidades")
    void testPagoEnEstablecimiento(double cantidad) {
        try {
            credito.pagoEnEstablecimiento(COMERCIO, cantidad);
            assertThat(credito.mMovimientos.size(), is(1));
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    @Test
    void testSaldoTotal() throws Exception {
        double total = cantidades().mapToDouble(Double::doubleValue).sum();
        credito.ingresar(total);
        assertEquals(total, credito.getSaldo());
    }

    @ParameterizedTest(name = "Liquidación en mes {0} de {1}")
    @MethodSource("mesesAnos")
    void testLiquidacion(int mes, int ano) throws Exception {
        credito.ingresar(80D);
        
        int numMovimientosAntes = credito.mCuentaAsociada.mMovimientos.size();
        credito.liquidar(mes, ano);
        int numMovimientosDespues = credito.mCuentaAsociada.mMovimientos.size();
        Movimiento ultimo = (Movimiento) credito.mCuentaAsociada.mMovimientos.lastElement();
        assertTrue(numMovimientosAntes < numMovimientosDespues || ultimo.getImporte() == 0);
    }

    static Stream<Double> cantidades() {
        return Stream.of(0D, 50D, 100D, 2000D, 5000D);
    }

    static Stream<Arguments> mesesAnos() {
        return Stream.of(
            Arguments.of(1, 2024),
            Arguments.of(2, 2024),
            Arguments.of(3, 2024),
            Arguments.of(1, 2025),
            Arguments.of(2, 2025),
            Arguments.of(3, 2025)
        );
    }
}
