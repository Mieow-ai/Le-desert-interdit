import static org.junit.Assert.*;

import modele.*;
import org.junit.Test;

public class MainTest {
    @Test
    public void PlateauTest() {
        Plateau p = new Plateau();
        modele.Joueur j = modele.Joueur.creerJoueur(modele.Joueur.Role.METEOROLOGUE, "", p);
        Zone oeil = p.getZone(2,2);
        j.setZone(p.getZone(2,1));
        assertThrows("Creuser oeil",IllegalStateException.class,()->j.creuserZone(oeil));
    }

    @Test
    public void tunnelProtege() {
        Plateau p = new Plateau();
        modele.Joueur j1 = modele.Joueur.creerJoueur(Joueur.Role.ALPINISTE, "", p);
        modele.Joueur j2 = modele.Joueur.creerJoueur(Joueur.Role.EXPLORATEUR, "", p);
        Zone zoneTunnel1 = Zone.creerZone(0,0,Zone.TypeZone.TUNNEL,' ', p);
        p.placerZonePourTest(0,0,zoneTunnel1);
        j1.setZone(zoneTunnel1);
        j2.setZone(zoneTunnel1);
        assertEquals("TUNNEL Les joueurs ont 4 en niveau d'eau",4, j1.getNvEau());
        assertEquals("TUNNEL Les joueurs ont 4 en niveau d'eau",4, j2.getNvEau());
        p.vagueDeChaleur();
        assertEquals("TUNNEL Les joueurs ont 3 en niveau d'eau",3, j1.getNvEau());
        assertEquals("TUNNEL Les joueurs ont 3 en niveau d'eau",3, j2.getNvEau());
        assertEquals("TUNNEL La zone ne protege pas de la chaleur",false, zoneTunnel1.protegeChaleur());
        zoneTunnel1.reveler();
        assertEquals("TUNNEL La zone protege de la chaleur",true, zoneTunnel1.protegeChaleur());
        p.vagueDeChaleur();
        assertEquals("Les joueurs ont toujours 3 en niveau d'eau",3, j1.getNvEau());
        assertEquals("Les joueurs ont toujours 3 en niveau d'eau",3, j2.getNvEau());
        p.vagueDeChaleur();
        j2.setZone(p.getZone(0,1));
        assertEquals("j1 toujours protégé le tunnel",3, j1.getNvEau());
        assertEquals("j2 n'est plus protégé par le tunnel",3, j2.getNvEau());

    }

    @Test
    public void tunnelDeplacement() {
        Plateau p = new Plateau();
        modele.Joueur j1 = modele.Joueur.creerJoueur(Joueur.Role.METEOROLOGUE, "", p);
        Zone zoneTunnel1 = Zone.creerZone(0,0,Zone.TypeZone.TUNNEL,' ', p);
        Zone zoneTunnel2 = Zone.creerZone(4,4,Zone.TypeZone.TUNNEL,' ', p);
        p.placerZonePourTest(0,0,zoneTunnel1);
        j1.setZone(zoneTunnel1);
        p.placerZonePourTest(4,4,zoneTunnel2);

        assertEquals("TUNNEL j1 a bien seulement 2 cases accessibles (il est dans le coin)",2, j1.getZonesAccessibles(p).size());
        zoneTunnel1.reveler();
        assertFalse("TUNNEL j1 ne peut accéder au second tunnel", j1.getZonesAccessibles(p).contains(zoneTunnel2));
        assertEquals("TUNNEL j1 a toujours 2 cases accessibles (il est dans le coin)",2, j1.getZonesAccessibles(p).size());
        zoneTunnel2.reveler();
        assertTrue("TUNNEL j1 peut accéder au second tunnel", j1.getZonesAccessibles(p).contains(zoneTunnel2));
        zoneTunnel2.ajouterSable(1);
        assertFalse("TUNNEL par contre il ne peut pas creuser le second tunnel a distance", j1.getZonesCreusables(p).contains(zoneTunnel2));
        zoneTunnel2.ajouterSable(1);
        assertFalse("TUNNEL j1 ne peut pas accéder au second tunnel (ensablé)", j1.getZonesAccessibles(p).contains(zoneTunnel2));
        modele.Joueur alpiniste = modele.Joueur.creerJoueur(Joueur.Role.ALPINISTE, "", p);
        alpiniste.setZone(zoneTunnel1);
        assertTrue("TUNNEL alpiniste peut accéder au second tunnel (même ensablé)", alpiniste.getZonesAccessibles(p).contains(zoneTunnel2));
        alpiniste.setZone(zoneTunnel2);
        assertTrue("TUNNEL j1  peut accéder au second tunnel ensablé si alpiniste", j1.getZonesAccessibles(p).contains(zoneTunnel2));

        // Si le joueur recommence une partie
        Plateau p2 = new Plateau();
        assertFalse("TUNNEL verification que l'attribut statique est bien remis à 0 lors de la création d'un nouveau PLateau", j1.getZonesAccessibles(p).contains(zoneTunnel2));
    }

    @Test
    public void testIntersectionOeilTempete() {
        Plateau p = new Plateau();
        int oeilI = p.getOeilI(); // 2
        int oeilJ = p.getOeilJ(); // 2
        Zone.TypeZone type = Zone.TypeZone.INDICE_BOITE;
        Zone indiceI = Zone.creerZone(oeilI, 0, type, 'i', p);

        Zone indiceJ = Zone.creerZone(0, oeilJ, type, 'j', p);

        p.placerZonePourTest(oeilI, 0, indiceI);
        p.placerZonePourTest(0, oeilJ, indiceJ);

        indiceI.reveler();
        indiceJ.reveler();

        assertNull("L'intersection est toujours l'oeil", p.getZone(oeilI, oeilJ));
        assertTrue("L'objet est en attente", p.testGetObjetsEnAttente().contains(type));
        p.leVentSouffle(CarteTempete.Direction.SUD, 1); // On déplace l'oeil

        assertSame("L'objet est bien sur la Zone (2,2)", type, p.getZone(2, 2).get_objet_pose());
        assertTrue("L'attente est vide", p.testGetObjetsEnAttente().isEmpty());

    }

    // Test Spécifiques aux rôles

    // Test Spécifique à l'Alpiniste
    @Test
    public void testDeplacementAlpiniste() {
        Plateau p = new Plateau();
        modele.Joueur alpiniste = modele.Joueur.creerJoueur(modele.Joueur.Role.ALPINISTE, "", p);
        modele.Joueur j2 = modele.Joueur.creerJoueur(modele.Joueur.Role.METEOROLOGUE, "", p);
        Zone base = p.getZone(0, 0);
        alpiniste.setZone(base);
        j2.setZone(base);
        Zone voisin = p.getZone(0, 1);
        voisin.viderSable();
        assertTrue("Zone sans sable accessible pour les deux joueurs", alpiniste.getZonesAccessibles(p).contains(voisin) && j2.getZonesAccessibles(p).contains(voisin));
        voisin.ajouterSable(1);
        assertTrue("Zone avec 1 sable accessible pour les deux joueurs", alpiniste.getZonesAccessibles(p).contains(voisin));
        voisin.ajouterSable(1);
        assertTrue("Zone avec >= 2 sable  accessible pour l'alpiniste", alpiniste.getZonesAccessibles(p).contains(voisin));
        assertFalse("Zone avec >= 2 sable  non accessible pour le météorologue", j2.getZonesAccessibles(p).contains(voisin));
        alpiniste.setZone(voisin);
        assertTrue("Zone avec >= 2 sable  accessible lorsque l'alpiniste est dessus", j2.getZonesAccessibles(p).contains(voisin));
        j2.setZone(voisin);
        assertFalse("Le joueur peut sortir d'une case ensablée si l'alpiniste est dessus", j2.getZonesAccessibles(p).isEmpty());
        alpiniste.setZone(base);
        assertTrue("Le joueur ne peut pas sortir d'une case ensablée si l'alpiniste est dessus", j2.getZonesAccessibles(p).isEmpty());
    }

    // Tests Spécifiques à l'Explorateur
    @Test
    public void testDeplacementExplorateur() {
        Plateau p = new Plateau();
        modele.Joueur explo = modele.Joueur.creerJoueur(Joueur.Role.EXPLORATEUR, "", p);
        modele.Joueur j2 = modele.Joueur.creerJoueur(modele.Joueur.Role.METEOROLOGUE, "", p);
        Zone base = p.getZone(0, 0);
        explo.setZone(base);
        j2.setZone(base);
        Zone voisin = p.getZone(1, 1);
        voisin.viderSable();
        assertTrue("Zone en diagonale accessible pour l'explorateur", explo.getZonesAccessibles(p).contains(voisin));
        assertFalse("Zone en diagonale non accessible pour le lambda", j2.getZonesAccessibles(p).contains(voisin));

        voisin.ajouterSable(2);
        assertFalse("Zone en diagonale ensablée non accessible pour l'explorateur", explo.getZonesAccessibles(p).contains(voisin));
        modele.Joueur alpiniste = modele.Joueur.creerJoueur(modele.Joueur.Role.ALPINISTE, "", p);
        alpiniste.setZone(voisin);
        assertTrue("Zone en diagonale ensablée accessible pour l'explorateur si l'alpiniste est dessus", explo.getZonesAccessibles(p).contains(voisin));
    }

    @Test
    public void testCreusableExplorateur() {
        Plateau p = new Plateau();
        modele.Joueur explo = modele.Joueur.creerJoueur(Joueur.Role.EXPLORATEUR, "", p);
        modele.Joueur j2 = modele.Joueur.creerJoueur(modele.Joueur.Role.METEOROLOGUE, "", p);
        Zone base = p.getZone(0, 0);
        explo.setZone(base);
        j2.setZone(base);
        Zone voisin = p.getZone(1, 1);
        voisin.viderSable();
        assertTrue("Zone en diagonale sans sable  creusable pour l'explorateur", explo.getZonesCreusables(p).contains(voisin));
        voisin.reveler();
        assertFalse("Zone en diagonale sans sable et révélée non creusable pour l'explorateur", explo.getZonesCreusables(p).contains(voisin));
        voisin.ajouterSable(2);
        assertFalse("Zone en diagonale non creusable pour le lambda", j2.getZonesCreusables(p).contains(voisin));
        assertTrue("Zone en diagonale avec sable creusable pour l'explorateur", explo.getZonesCreusables(p).contains(voisin));
    }

    // Test archéologue
    @Test
    public void testCreuserArcheologue() {
        Plateau p = new Plateau();
        modele.Joueur archeo = modele.Joueur.creerJoueur(Joueur.Role.ARCHEOLOGUE, "", p);
        modele.Joueur j2 = modele.Joueur.creerJoueur(modele.Joueur.Role.METEOROLOGUE, "", p);
        Zone base = p.getZone(0, 0);
        archeo.setZone(base);
        j2.setZone(base);
        Zone voisin = p.getZone(1, 0);
        voisin.viderSable();
        voisin.ajouterSable(2);
        archeo.creuserZone(voisin);
        assertEquals("L'archéologue a enlever 2 sables", 0, voisin.getNbSable());
        voisin.ajouterSable(2);
        j2.creuserZone(voisin);
        assertEquals("Le lambda a enlevé 1 sable", 1, voisin.getNbSable());
        archeo.creuserZone(voisin);
        assertEquals("L'archéologue a enlever 1 sable car il en restait qu'un", 0, voisin.getNbSable());
    }
    // Test méteorologue
    @Test
    public void testAmortirMeteorologue() {
        Plateau p = new Plateau();
        modele.Joueur meteoro = modele.Joueur.creerJoueur(modele.Joueur.Role.METEOROLOGUE, "", p);
        modele.Joueur lambda = modele.Joueur.creerJoueur(Joueur.Role.ALPINISTE, "", p);
        meteoro.amortirTempete();
        assertEquals("L'archéologue amorti la tempête",1, meteoro.getAndResetReductionTempete());
        assertEquals("Le get amorti reset bie,",0, meteoro.getAndResetReductionTempete());
        assertThrows("Autre amorti",IllegalStateException.class,()->lambda.amortirTempete());
    }

    // Les déplacements de la navigatrice nécessitent le controleur et les boutons spéciaux
    // Une fois que le joueur est choisi le déplacement dépend entièrement du joueur par délégation donc
    // Les joueurs peuvent être déplacés dans les bonnes cases (ensablées et accompagné pour l'alpiniste,
    // en diagonale pour l'explorateur etc

    // Test Porteuse d'eau
    @Test
    public void testPorteuseEau() {
        Plateau p = new Plateau();
        modele.Joueur porteuse = modele.Joueur.creerJoueur(Joueur.Role.PORTEUSE_EAU, "", p);
        modele.Joueur lambda = modele.Joueur.creerJoueur(Joueur.Role.ALPINISTE, "", p);
        assertEquals("nv Eau 5 pour la porteuse", 5, porteuse.getNvEau());
        assertEquals("nv Eau 4 pour le lambda", 4, lambda.getNvEau());
        
        porteuse.setZone(p.getZone(0,0));
        lambda.setZone(p.getZone(0,0));
        
        lambda.boire_eau(-1);
        porteuse.donner_eau(lambda);
        assertEquals("Donner de l'eau ne coute pas d'action à la porteuse", 4, porteuse.getNb_action_restante());
        assertEquals("Lambda a bien gagné un niveau d'eau", 4, lambda.getNvEau());
        assertEquals("La porteuse a bien perdu 1 d'eau en donnant", 4, porteuse.getNvEau());
        
        lambda.boire_eau(-1); // Perd 1
        porteuse.setZone(p.getZone(1,0));
        porteuse.donner_eau(lambda);
        assertEquals("Lambda a bien gagné un niveau d'eau (case adjacente)", 4, lambda.getNvEau());

        lambda.boire_eau(-2); // Lambda n'a plus que 2
        porteuse.boire_eau(-2); // Porteuse n'a plus que 1
        
        Zone eau = Zone.creerZone(0, 0, Zone.TypeZone.EAU, ' ', p);
        porteuse.setZone(eau);
        lambda.setZone(eau);
        
        porteuse.creuserZone(eau);
        assertEquals("Lambda a bien gagné deux niveaux d'eau ", 4, lambda.getNvEau());
        assertEquals("Porteuse a bien gagné deux niveaux d'eau ", 3, porteuse.getNvEau());
        
        lambda.boire_eau(-2); // Reste 2
        porteuse.boire_eau(-2); // Reste 1
        

        porteuse.creuserZone(eau);
        
        assertEquals("Lambda ne doit pas regagner d'eau ", 2, lambda.getNvEau());
        assertEquals("Porteuse a bien regagné deux niveaux d'eau", 3, porteuse.getNvEau());
    }



    // Test Spécifiques aux équipements
    @Test
    public void obtenirEquipement() {
        Plateau p = new Plateau();
        modele.Joueur lambda = modele.Joueur.creerJoueur(Joueur.Role.ALPINISTE, "", p);
        Zone zoneEquip = Zone.creerZone(0,0, Zone.TypeZone.ENGRENAGE, ' ', p);
        zoneEquip.viderSable();
        assertTrue("Le joueur n'a pas d'équipements",lambda.get_equipement().isEmpty());
        lambda.creuserZone(zoneEquip);
        assertEquals("Le joueur a 1 equipement",1,lambda.get_equipement().size());
    }

    @Test
    public void zonesCreusablesBlaster() {
        Plateau p = new Plateau();
        modele.Joueur lambda = modele.Joueur.creerJoueur(Joueur.Role.ALPINISTE, "", p);
        lambda.get_equipement().add(new CarteEquipement(CarteEquipement.Type.BLASTER_DES_DUNES));
        lambda.setZone(p.getZone(0,0));
        Zone voisin = p.getZone(1, 0);
        voisin.viderSable();
        voisin.ajouterSable(3);
        assertEquals(3,voisin.getNbSable());
        assertTrue("La zone voisin est creusable",lambda.getZonesCreusables(p).contains(voisin));
        // Ensuite le blaster utilise viderSable
        voisin.viderSable();
        assertEquals(0, voisin.getNbSable());
    }

    @Test
    public void testToutesZonesJetpack() {
        Plateau p = new Plateau();
        assertEquals("24 cases accessibles avec le jetpack (toutes sauf l'oeil)",24, p.get_toutes_zones().size());
    }

    @Test
    public void testZonesRegardableTerrascope() {
        Plateau p = new Plateau();
        assertEquals("2 cases peuvent êtres regardées avec le Terrascope (toutes sauf l'oeil et la zone crash)",23, p.get_zones_cachees().size());
        p.revelerToutLePlateau();
        assertEquals("Aucune case regardable",0, p.get_zones_cachees().size());
    }

    @Test
    public void testAccelerateurTemps() {
        Plateau p = new Plateau();
        modele.Joueur lambda = modele.Joueur.creerJoueur(Joueur.Role.ALPINISTE, "", p);
        lambda.setZone(p.getZone(0,0));
        assertEquals("Le joueur a 4 actions restantes",4, lambda.getNb_action_restante());
        lambda.ajouter_actions_bonus(2);
        assertEquals("Le joueur a 6 actions restantes",6, lambda.getNb_action_restante());
        p.finDeTour();
        assertEquals("Si le joueur n'utilise pas ses actions supplémentaires alors a la fin du tour il les perds",4,lambda.getNb_action_restante());
    }

    @Test
    public void testBouclierSolaire() {
        Plateau p = new Plateau();
        modele.Joueur j1 = modele.Joueur.creerJoueur(Joueur.Role.ALPINISTE, "", p);
        modele.Joueur j2 = modele.Joueur.creerJoueur(Joueur.Role.EXPLORATEUR, "", p);
        Zone zone = p.getZone(0,0);
        j1.setZone(zone);
        j2.setZone(zone);
        assertEquals("Les joueurs ont 4 en niveau d'eau",4, j1.getNvEau());
        assertEquals("Les joueurs ont 4 en niveau d'eau",4, j2.getNvEau());
        p.vagueDeChaleur();
        assertEquals("Les joueurs ont 3 en niveau d'eau",3, j1.getNvEau());
        assertEquals("Les joueurs ont 3 en niveau d'eau",3, j2.getNvEau());
        assertEquals("La zone ne protege pas de la chaleur",false, zone.protegeChaleur());
        zone.set_protege_par_bouclier(true);
        assertEquals("La zone protege de la chaleur",true, zone.protegeChaleur());
        p.vagueDeChaleur();
        assertEquals("Les joueurs ont toujours 3 en niveau d'eau",3, j1.getNvEau());
        assertEquals("Les joueurs ont toujours 3 en niveau d'eau",3, j2.getNvEau());
        j2.setZone(p.getZone(0,1));
        p.vagueDeChaleur();
        assertEquals("j1 toujours protégé le bouclier",3, j1.getNvEau());
        assertEquals("j2 n'est plus protégé par le bouclier",2, j2.getNvEau());
    }

    // L'équipement Réserve d'eau utilise la fonction générale Boire Eau


    // Tests relatifs aux fins de parties
    @Test
    public void testVagueChaleur() {
        Plateau p = new Plateau();
        modele.Joueur j1 = modele.Joueur.creerJoueur(Joueur.Role.ALPINISTE, "", p);
        Zone zone = p.getZone(0,0);
        j1.setZone(zone);
        assertEquals("La partie est en cours",Plateau.EtatPartie.EN_COURS, p.getEtat());
        for (int i = 0; i <4 ; i++) {
            p.vagueDeChaleur();
            //Le joueur doit mourir de soif -> fin de la partie
        }
        assertEquals("Le joueur n'a plus d'eau", 0, j1.getNvEau());
        assertEquals("La partie est en mode défaite",Plateau.EtatPartie.DEFAITE, p.getEtat());
    }

    @Test
    public void testMortEnsablee() {
        Plateau p = new Plateau();
        modele.Joueur j1 = modele.Joueur.creerJoueur(Joueur.Role.ALPINISTE, "", p);
        Zone zone = p.getZone(0,0);
        j1.setZone(zone);
        assertEquals("La partie est en cours",Plateau.EtatPartie.EN_COURS, p.getEtat());
        p.enlever_sable(8);
        p.ajouterSable(zone, 49);
        assertEquals("La partie est en mode défaite",Plateau.EtatPartie.DEFAITE, p.getEtat());
    }

    @Test
    public void testMortNiveauTempete() {
        Plateau p = new Plateau();
        modele.Joueur j1 = modele.Joueur.creerJoueur(Joueur.Role.ALPINISTE, "", p);
        Zone zone = p.getZone(0,0);
        j1.setZone(zone);
        assertEquals("La partie est en cours",Plateau.EtatPartie.EN_COURS, p.getEtat());
        for(int i = 0;i<6;i++) {
            p.LaTempeteSeDechaine();
        }
        assertEquals("La partie est en mode défaite",Plateau.EtatPartie.DEFAITE, p.getEtat());
    }

    @Test
    public void TestVictoire() {
        Plateau p = new Plateau();
        modele.Joueur j1 = modele.Joueur.creerJoueur(Joueur.Role.ALPINISTE, "", p);
        Zone z_deco  = Zone.creerZone(0, 0, Zone.TypeZone.DECOLLAGE,' ', p);
        j1.setZone(z_deco);
        assertEquals("La partie est en cours",Plateau.EtatPartie.EN_COURS, p.getEtat());
        for(Plateau.PIECE piece : Plateau.PIECE.values()){
            p.add_piece(piece);
        }
        p.verifier_victoire();
        assertEquals("La partie est en mode victoire",Plateau.EtatPartie.VICTOIRE, p.getEtat());
    }





}
