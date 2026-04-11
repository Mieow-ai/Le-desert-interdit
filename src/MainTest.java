import static org.junit.Assert.*;

import modele.CarteTempete;
import modele.Zone;
import org.junit.Test;

import modele.Plateau;


public class MainTest {
    @Test
    public void PlateauTest(){
        Plateau p = new Plateau();
    }


    @Test
    public void testIntersectionOeilTempete() {
        Plateau p = new Plateau();
        int oeilI= p.getOeilI();  // 2
        int oeilJ = p.getOeilJ();  //  2
        Zone.TypeZone type =Zone.TypeZone.INDICE_BOITE;
        Zone indiceI = Zone.creerZone(oeilI, 0, type, 'i', p);

        Zone indiceJ = Zone.creerZone(0, oeilJ, type, 'j',p);

        p.placerZonePourTest(oeilI, 0, indiceI);
        p.placerZonePourTest(0, oeilJ, indiceJ);

        indiceI.reveler();
        indiceJ.reveler();

        assertNull("L'intersection est toujours l'oeil", p.getZone(oeilI, oeilJ));

        assertTrue("L'objet est en attente", p.testGetObjetsEnAttente().contains(type));

        p.leVentSouffle( CarteTempete.Direction.SUD, 1); // On déplace l'oeil

        assertSame("L'objet est bien sur la Zone (2,2)", type, p.getZone(2, 2).get_objet_pose());
        assertTrue("L'attente est vide", p.testGetObjetsEnAttente().isEmpty());



        }
}
