package at.schrer.inject.dummyclasses.safe.name;

import at.schrer.inject.annotations.Component;

import static at.schrer.inject.dummyclasses.safe.name.NamingInterface.EVENMORENAMEDDUMMY;

@Component(name = EVENMORENAMEDDUMMY)
public class EvenMoreNamedDummy implements NamingInterface {
}