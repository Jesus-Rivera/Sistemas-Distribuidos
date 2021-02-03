
CREATE DATABASE IF NOT EXISTS `ServidorTiempo` DEFAULT CHARACTER SET utf8 ;
USE `ServidorTiempo` ;

CREATE TABLE IF NOT EXISTS `ServidorTiempo`.`HoraCentral` (
  `idHoraCentral` INT NOT NULL,
  `HoraPrev` TIME NULL,
  `HoraRef` TIME NULL,
  PRIMARY KEY (`idHoraCentral`))
ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS `ServidorTiempo`.`Equipo` (
  `idEquipo` INT NOT NULL,
  `IP` VARBINARY(4) NOT NULL,
  `Nombre` VARCHAR(45) NOT NULL,
  `Latencia` TIME NOT NULL,
  PRIMARY KEY (`idEquipo`),
  UNIQUE INDEX `IP_UNIQUE` (`IP` ASC),
  UNIQUE INDEX `Nombre_UNIQUE` (`Nombre` ASC))
ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS `ServidorTiempo`.`HoraEquipo` (
  `idHoraEquipo` INT NOT NULL,
  `hEquipo` TIME NULL,
  `aEquipo` TIME NULL,
  `Equipo_idEquipo` INT NOT NULL,
  `HoraCentral_idHoraCentral` INT NOT NULL,
  PRIMARY KEY (`idHoraEquipo`),
  INDEX `fk_HoraEquipo_Equipo1_idx` (`Equipo_idEquipo` ASC),
  INDEX `fk_HoraEquipo_HoraCentral1_idx` (`HoraCentral_idHoraCentral` ASC),
  CONSTRAINT `fk_HoraEquipo_Equipo1`
    FOREIGN KEY (`Equipo_idEquipo`)
    REFERENCES `ServidorTiempo`.`Equipo` (`idEquipo`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_HoraEquipo_HoraCentral1`
    FOREIGN KEY (`HoraCentral_idHoraCentral`)
    REFERENCES `ServidorTiempo`.`HoraCentral` (`idHoraCentral`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;