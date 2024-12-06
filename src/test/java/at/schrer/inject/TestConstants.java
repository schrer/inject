package at.schrer.inject;

import java.util.List;

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

        String NAMED_PACKAGE = SAFE_PACKAGE + ".name";
        String BEANSOURCE_PACKAGE = SAFE_PACKAGE + ".beansource";
        String BEANSOURCEDEP_PACKAGE = SAFE_PACKAGE + ".beansourcedeps";
        String UNNAMED_STRING_PACKAGE = ILLEGAL_PACKAGE + ".unnamedstring";
    }

    public interface ComponentNamesBeanSourcePack {
        String AC_INST1 = "inst1";
        String AC_INST2 = "inst2";
        String AC_INST3 = "inst3";
        String AC_INST4 = "inst4";
        String VAL_1 = "val1";
        String VAL_2 = "val2";
        List<String> BEANSOURCE_NAMES = List.of(AC_INST1, AC_INST2, AC_INST3, AC_INST4, VAL_1, VAL_2);
    }

    public interface ComponentNamesBeanSourceDepPack {
        String TC1 = "one";
        String TC2 = "two";
        String TC3 = "three";
    }
}
