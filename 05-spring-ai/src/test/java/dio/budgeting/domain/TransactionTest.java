package dio.budgeting.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TransactionTest {

    @Test
    void deveCriarTransacaoValida() {
        var transaction = new Transaction("Compra no mercado", 5000, Category.GROCERIES);

        assertEquals("Compra no mercado", transaction.getDescription());
        assertEquals(5000, transaction.getAmount());
        assertEquals(Category.GROCERIES, transaction.getCategory());
    }

    @Test
    void naoDevePermitirValorZero() {
        assertThrows(IllegalArgumentException.class, () ->
                new Transaction("Compra no mercado", 0, Category.GROCERIES));
    }

    @Test
    void naoDevePermitirValorNegativo() {
        assertThrows(IllegalArgumentException.class, () ->
                new Transaction("Compra no mercado", -100, Category.GROCERIES));
    }

    @Test
    void naoDevePermitirDescricaoVazia() {
        assertThrows(IllegalArgumentException.class, () ->
                new Transaction("", 5000, Category.GROCERIES));
    }

    @Test
    void naoDevePermitirDescricaoNula() {
        assertThrows(IllegalArgumentException.class, () ->
                new Transaction(null, 5000, Category.GROCERIES));
    }
}