package at.schrer.inject.dummyclasses.safe.name;

import at.schrer.inject.annotations.Component;

import static at.schrer.inject.dummyclasses.safe.name.NamingInterface.NAMEDDUMMY;

@Component(name = NAMEDDUMMY)
public class NamedDummy implements NamingInterface {
}
