package at.schrer.inject.dummyclasses.safe.name;

import at.schrer.inject.annotations.Component;

import static at.schrer.inject.dummyclasses.safe.name.NamingInterface.MORENAMEDDUMMY;

@Component(name = MORENAMEDDUMMY)
public class MoreNamedDummy implements NamingInterface {
}
