// algorithm by justin jagieniak
// TODO: extract into own package

if (typeof Prefixes === "undefined") {
  const Prefixes = {
    // region Upper
    deca: "da",
    hecto: "h",
    kilo: "k",
    mega: "M",
    giga: "G",
    tera: "T",
    peta: "P",
    exa: "E",
    zetta: "Z",
    yotta: "Y",
    // endregion

    // region Lower
    deci: "d",
    centi: "c",
    milli: "m",
    micro: "\u00B5",
    nano: "n",
    pico: "p",
    femto: "f",
    atto: "a",
    zepto: "z",
    yocto: "y",
    // endregion

    has(val) {
      return Prefixes.hasOwnProperty(val.toLowerCase());
    },

    get(val) {
      return Prefixes[val.toLowerCase()];
    },
  };
}

if (typeof Units === "undefined") {
  const Units = {
    // region Base SI
    metre: "m",
    kilogram: "kg",
    second: "s",
    ampere: "A",
    kelvin: "\u212A",
    mole: "mol",
    candela: "cd",
    // endregion

    // region Extended Base SI
    one: "1",
    day: "d",
    hour: "h",
    minute: "min",
    degree: "\u00B0",
    arcminute: "\u2032",
    arcsecond: "\u2033",
    // endregion

    // region Derived SI
    gram: "g",
    radian: "rad",
    steradian: "sr",
    hertz: "Hz",
    newton: "N",
    pascal: "Pa",
    joule: "J",
    watt: "W",
    coulomb: "C",
    volt: "V",
    farad: "F",
    ohm: "\u2126",
    siemens: "S",
    weber: "Wb",
    tesla: "T",
    henry: "H",
    degreecelsius: "\u2103",
    lumen: "lm",
    lux: "lx",
    becquerel: "Bq",
    sievert: "Sv",
    gray: "Gy",
    katal: "kat",
    // endregion

    // region Non SI
    hectar: "ha",
    litre: "l",
    tonne: "t",
    electronvolt: "eV",
    dalton: "Da",
    atomicmassunit: "u",
    astronomicalunit: "au",
    cligth: "<msub><mi>c</mi><mi>u</mi></msub>",
    planckbar: "\u210F",
    electronmass: "<msub><mi>m</mi><mi>e</mi></msub>",
    naturalunittime:
        "<mrow><mi>\u210F</mi><mo>/</mo><mo>(</mo><mrow><msub><mi>m</mi><mi>e</mi></msub><msup><msub><mi>c</mi><mn>0</mn></msub><mn>2</mn></msup></mrow><mo>)</mo></mrow>",
    elementarycharge: "e",
    bohr: "<msub><mi>a</mi><mn>0</mn></msub>",
    hartree: "<msub><mi>E</mi><mi>h</mi></msub>",
    atomicunittime:
        "<mrow><mi>\u210F</mi><mo>/</mo><mo>(</mo><msub><mi>E</mi><mi>h</mi></msub><mo>)</mo></mrow>",
    bar: "bar",
    mmhg: "mmHg",
    angstrom: "\u212B",
    nauticalmile: "M",
    barn: "b",
    knot: "kn",
    nepper: "Np",
    bel: "B",
    decibel: "dB",
    // endregion

    // region Others

    percent: "%",

    // endregion

    has(val) {
      return Units.hasOwnProperty(val.toLowerCase());
    },

    get(val) {
      return Units[val.toLowerCase()];
    },
  };
}

if (typeof Operators === "undefined") {
  const Operators = {
    "^": "<msup>$value<mn>$n</mn></msup>",
    _: "<msub>$value<mn>$n</mn></msub>",
  };
}

if (typeof Exponents === "undefined") {
  let Exponents = {
    tothe: "<msup>$value<mn>$n</mn></msup>",

    isExponent(candidate) {
      if (!candidate) return false;

      // check if value is inside of {}
      if (candidate.indexOf("{") > 0) {
        let str = candidate.substring(0, candidate.indexOf("{"));
        return this.hasOwnProperty(str.toLowerCase());
      } else {
        return this.hasOwnProperty(candidate.substr(0, 1));
      }
    },
    getValue(exponent) {
      let index = exponent.indexOf("{");
      let str;
      let value;
      if (index > 0) {
        // value is between { and }
        str = exponent.substring(0, index);
        value = exponent.substring(index + 1, exponent.indexOf("}"));
        return this[str].replace("$n", value);
      } else {
        // exponent is only one char long
        str = exponent.substr(0, 1);
        value = exponent.substring(1);
        return this[str].replace("$n", value);
      }
    },
    // Operators
    "^": "<msup>$value<mn>$n</mn></msup>",
    _: "<msub>$value<mn>$n</mn></msub>",
  };
}

if (typeof SiUnit === "undefined") {
  class SiUnit {
    constructor(unit = "", prefix = "", exponent = "") {
      this.prefix = prefix;
      this.unit = unit;
      this.exponent = exponent;
    }

    toString() {
      if (this.exponent) {
        return `<math>${this.exponent.replace(
            "$value",
            "<mi>" + this.prefix + this.unit + "</mi>"
        )}</math>`;
      } else {
        return `<math><mi>${this.prefix}${this.unit}</mi></math>`;
      }
    }
  }
}

function prepareStr(value) {
  let operatorKeys = "";
  for (let opValue of Object.keys(Operators)) operatorKeys += `\\${opValue}`;
  // adds a backslash to _ and ^
  return value.replace(new RegExp(`([${operatorKeys}])`), "\\$1");
}

function convertSiUnitToMathMl(value) {
  let siUnits = [];
  let siUnit = new SiUnit();
  value = prepareStr(value);
  let parts = value.split("\\").filter((x) => x);

  for (let i = 0; i < parts.length; i++) {
    let part = parts[i];
    if (!siUnit.prefix && Prefixes.has(part)) {
      siUnit.prefix = Prefixes.get(part);
    } else if (!siUnit.unit && Units.has(part)) {
      siUnit.unit = Units.get(part);
      if (!Exponents.isExponent(parts[i + 1])) {
        siUnits.push(siUnit);
        siUnit = new SiUnit();
      }
    } else if (siUnit.unit && Exponents.isExponent(part)) {
      // append STRING to exponent
      if (siUnit.exponent) {
        siUnit.exponent = Exponents.getValue(part).replace(
            "$value",
            "siUnit.exponent"
        );
      } else {
        siUnit.exponent = Exponents.getValue(part);
      }

      if (!Exponents.isExponent(parts[i + 1])) {
        siUnits.push(siUnit);
        siUnit = new SiUnit();
      }
    } else {
      console.error("Unknown or invalid value: " + part);
    }
  }

  return siUnits;
}