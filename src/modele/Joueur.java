package modele;

public abstract class Joueur {
    private String nom;
    private int niveauEau;
    private Zone zone;
}


class Archeologue extends Joueur {
}

class Alpiniste extends Joueur {
}

class Explorateur extends Joueur {
}

class Meteorologue extends Joueur {
}