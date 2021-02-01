CREATE DATABASE IF NOT EXISTS `Servidor` DEFAULT CHARACTER SET utf8 ;
USE `Servidor` ;

CREATE TABLE IF NOT EXISTS `Servidor`.`Libro` (
  `idLibro` INT NOT NULL,
  `Nombre` VARCHAR(45) NOT NULL,
  `Autor` VARCHAR(45) NOT NULL,
  `Precio` FLOAT NULL,
  `Portada` LONGBLOB NULL,
  PRIMARY KEY (`idLibro`),
  UNIQUE INDEX `Nombre_UNIQUE` (`Nombre` ASC))
ENGINE = InnoDB;


CREATE TABLE IF NOT EXISTS `Servidor`.`Usuario` (
  `idUsuario` INT NOT NULL,
  `IP` VARBINARY(4) NOT NULL,
  `Nombre` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`idUsuario`),
  UNIQUE INDEX `IP_UNIQUE` (`IP` ASC))
ENGINE = InnoDB;


CREATE TABLE IF NOT EXISTS `Servidor`.`Pedido` (
  `idPedido` INT NOT NULL,
  `Fecha` DATE NOT NULL,
  `Hora_inicio` TIME NULL,
  `Hora_final` TIME NULL,
  `Libro_idLibro` INT NULL,
  `Usuario_idUsuario` INT NULL,
  PRIMARY KEY (`idPedido`),
  INDEX `fk_Pedido_Libro1_idx` (`Libro_idLibro` ASC),
  INDEX `fk_Pedido_Usuario1_idx` (`Usuario_idUsuario` ASC),
  CONSTRAINT `fk_Pedido_Libro1`
    FOREIGN KEY (`Libro_idLibro`)
    REFERENCES `Servidor`.`Libro` (`idLibro`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Pedido_Usuario1`
    FOREIGN KEY (`Usuario_idUsuario`)
    REFERENCES `Servidor`.`Usuario` (`idUsuario`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;
