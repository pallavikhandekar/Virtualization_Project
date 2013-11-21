CREATE SCHEMA `cmpe283-analysisdata` ;
CREATE  TABLE `cmpe283-analysisdata`.`aggregation_data` (
  `idaggregation_data` INT NOT NULL ,
  `ipaddress` VARCHAR(45) NULL ,
  `host` VARCHAR(45) NULL,
  `cpuInformation` INT NULL ,
  `memoryInformation` INT NULL ,
  `storageInformation` INT NULL ,
  `networkInformation` INT NULL ,
  `cpuSpeed` INT NULL ,
  `runTimeInformation` INT NULL ,
  `timstamp` DATETIME NULL ,
  PRIMARY KEY (`idaggregation_data`) );

