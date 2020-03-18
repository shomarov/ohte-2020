package com.mycompany.unicafe;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import static org.hamcrest.core.Is.is;

public class MaksukorttiTest {

    Maksukortti kortti;

    @Before
    public void setUp() {
        kortti = new Maksukortti(1000);
    }

    @Test
    public void luotuKorttiOlemassa() {
        assertTrue(kortti != null);
    }

    @Test
    public void kortinSaldoAlussaOikein() {
        assertThat(1000, is(kortti.saldo()));
    }

    @Test
    public void rahanLataaminenKasvattaaSaldoaOiken() {
        kortti.lataaRahaa(100);
        assertThat("saldo: 11.0", is(kortti.toString()));
    }

    @Test
    public void rahanOttaminenToimii() {
        // saldo vähenee oikein, jos rahaa on tarpeeksi
        kortti.otaRahaa(100);
        assertThat("saldo: 9.0", is(kortti.toString()));

        // saldo ei muutu, jos rahaa ei ole tarpeeksi
        kortti.otaRahaa(1000);
        assertThat("saldo: 9.0", is(kortti.toString()));

        // metodi palauttaa true, jos rahat riittivät ja muuten false
        assertThat(true, is(kortti.otaRahaa(500)));
    }
}
