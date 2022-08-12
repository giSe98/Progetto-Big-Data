export class WorldLocationsRealTime {
  // get location of cities and capitals
  public static getAll(): any[] {
    if (this.locations.length === 0) {
      this.init();
    }
    return this.locations;
  }

  // get location of cities
  public static getCities(): any[] {
    if (this.cities.length === 0) {
      this.init();
    }
    return this.cities;
  }

  // get location of capitals
  public static getCapitals(): any[] {
    if (this.capitals.length === 0) {
      this.init();
    }
    return this.capitals;
  }

  // get countries
  public static getCountries(): any[] {
    if (this.countries.length === 0) {
      this.init();
    }
    return this.countries.filter((v, i, a) => a.indexOf(v) === i);
  }

  public static getCountrySum() {
    let ret = {}

    this.locations.forEach(el => {
      ret[el.country] = (ret[el.country] || 0) + 1;
    })

    var values = []
    for(var k in ret){
      values.push(ret[k]);
    }

    return values;
  }

  public static init() {
    this.locations = [
      {
        cap: true,
        pop: 1.875,
        lat: 48.2021179199219,
        lon: 16.3209857940674,
        country: "Austria",
        name: "Vienna"
      },
      {
        cap: false,
        pop: 3.025,
        lat: 1.22979354858398,
        lon: 104.177116394043,
        country: "Singapore",
        name: "Singapore"
      }
    ];

    this.capitals = this.locations.filter((city) => city.cap);
    this.cities = this.locations.filter((city) => !city.cap);
    this.countries = this.locations.map((get) => get.country);
    return this.locations;
  }

  private static locations: any[] = [];
  private static capitals: any[] = [];
  private static cities: any[] = [];
  private static countries: any[] = []
}
