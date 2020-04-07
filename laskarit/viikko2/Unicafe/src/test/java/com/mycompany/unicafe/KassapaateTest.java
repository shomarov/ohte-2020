package com.mycompany.unicafe;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class KassapaateTest {

    Kassapaate kassapaate;

    @Before
    public void setUp() {
        kassapaate = new Kassapaate();
    }

    @Test
    public void konstruktoriAsettaaRahanJaLounaidenMaaranOikein() {
        assertThat(100000, is(kassapaate.kassassaRahaa()));
        assertThat(0, is(kassapaate.edullisiaLounaitaMyyty()));
        assertThat(0, is(kassapaate.maukkaitaLounaitaMyyty()));
    }

    @Test
    public void kateisostoToimii() {
        // jos maksu riittävä: kassassa oleva rahamäärä kasvaa lounaan hinnalla ja
        // vaihtorahan suuruus on oikea
        int vaihtorahaEdullisesta = kassapaate.syoEdullisesti(500);
        assertThat(100240, is(kassapaate.kassassaRahaa()));
        assertThat(260, is(vaihtorahaEdullisesta));

        int vaihtorahaMaukkaasta = kassapaate.syoMaukkaasti(500);
        assertThat(100640, is(kassapaate.kassassaRahaa()));
        assertThat(100, is(vaihtorahaMaukkaasta));

        // jos maksu on riittävä: myytyjen lounaiden määrä kasvaa
        assertThat(1, is(kassapaate.edullisiaLounaitaMyyty()));
        assertThat(1, is(kassapaate.maukkaitaLounaitaMyyty()));

        // jos maksu ei ole riittävä: kassassa oleva rahamäärä ei muutu, kaikki rahat
        // palautetaan vaihtorahana ja myytyjen lounaiden määrässä ei muutosta
        int vaihtorahaRiittamattomastaEdullisesta = kassapaate.syoEdullisesti(100);
        assertThat(100, is(vaihtorahaRiittamattomastaEdullisesta));
        assertThat(100640, is(kassapaate.kassassaRahaa()));
        assertThat(1, is(kassapaate.edullisiaLounaitaMyyty()));

        int vaihtorahaRiittamattomastaMaukkaasta = kassapaate.syoMaukkaasti(300);
        assertThat(300, is(vaihtorahaRiittamattomastaMaukkaasta));
        assertThat(100640, is(kassapaate.kassassaRahaa()));
        assertThat(1, is(kassapaate.maukkaitaLounaitaMyyty()));
    }

    @Test
    public void korttiostoToimii() {
        Maksukortti kortti = new Maksukortti(10000);

        // jos kortilla on tarpeeksi rahaa, veloitetaan summa kortilta
        // ja palautetaan true
        assertThat(true, is(kassapaate.syoEdullisesti(kortti)));
        assertThat(9760, is(kortti.saldo()));
        assertThat(1, is(kassapaate.edullisiaLounaitaMyyty()));

        assertThat(true, is(kassapaate.syoMaukkaasti(kortti)));
        assertThat(9360, is(kortti.saldo()));
        assertThat(1, is(kassapaate.edullisiaLounaitaMyyty()));

        // jos kortilla ei ole tarpeeksi rahaa, kortin rahamäärä ei muutu, myytyjen
        // lounaiden määrä muuttumaton ja palautetaan false
        Maksukortti lataamatonKortti = new Maksukortti(0);

        assertThat(false, is(kassapaate.syoEdullisesti(lataamatonKortti)));
        assertThat(0, is(lataamatonKortti.saldo()));
        assertThat(1, is(kassapaate.edullisiaLounaitaMyyty()));

        assertThat(false, is(kassapaate.syoMaukkaasti(lataamatonKortti)));
        assertThat(0, is(lataamatonKortti.saldo()));
        assertThat(1, is(kassapaate.edullisiaLounaitaMyyty()));
    }

    @Test
    public void kortinLatausToimii() {
        Maksukortti lataamatonKortti = new Maksukortti(0);

        // kortille rahaa ladattaessa kortin saldo muuttuu ja kassassa oleva rahamäärä
        // kasvaa ladatulla summalla
        kassapaate.lataaRahaaKortille(lataamatonKortti, -5);
        assertThat(0, is(lataamatonKortti.saldo()));
        assertThat(100000, is(kassapaate.kassassaRahaa()));
        kassapaate.lataaRahaaKortille(lataamatonKortti, 10000);
        assertThat(10000, is(lataamatonKortti.saldo()));
        assertThat(110000, is(kassapaate.kassassaRahaa()));
    }
}
