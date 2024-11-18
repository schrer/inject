package at.schrer.inject;

public final class TestConstants {
    private TestConstants(){}

    public interface Packages {
        String DUMMYCLASSES_PACKAGE = "at.schrer.inject.dummyclasses";
        String ILLEGAL_PACKAGE = DUMMYCLASSES_PACKAGE + ".illegal";
        String SAFE_PACKAGE = DUMMYCLASSES_PACKAGE + ".safe";

        String NO_DEP_DUMMY_PACKAGE = SAFE_PACKAGE + ".depless";
        String NO_DEP_DUMMY_PACKAGE_SUB = NO_DEP_DUMMY_PACKAGE + ".sub";

        String YES_DEP_DUMMY_PACKAGE = SAFE_PACKAGE + ".yesdeps";
        String MULTISTEP_CYCLE_DEP_DUMMY_PACKAGE = ILLEGAL_PACKAGE + ".multistepcycle";
        String CYCLE_DEP_DUMMY_PACKAGE = ILLEGAL_PACKAGE + ".cycledep";
        String INTERFACE_PACKAGE = SAFE_PACKAGE + ".interfaces";

        String INTERDEP_PACKAGE = SAFE_PACKAGE + ".interdep";
        String INTERDEP_PACK1_PACKAGE = INTERDEP_PACKAGE + ".pack1";
        String INTERDEP_PACK2_PACKAGE = INTERDEP_PACKAGE + ".pack2";
    }
}
