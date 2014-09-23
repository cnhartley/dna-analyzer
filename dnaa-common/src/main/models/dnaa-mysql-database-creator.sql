SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

-- -----------------------------------------------------
-- Schema dnaa
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `dnaa` DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci ;
USE `dnaa` ;

-- -----------------------------------------------------
-- Table `dnaa`.`user`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `dnaa`.`user` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `username` VARCHAR(16) NOT NULL,
  `password` VARCHAR(32) NOT NULL,
  `create_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `email` VARCHAR(255) NOT NULL,
  `verified` TINYINT(1) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`));


-- -----------------------------------------------------
-- Table `dnaa`.`sequence_info`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `dnaa`.`sequence_info` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `user_id` INT UNSIGNED NOT NULL,
  `name` VARCHAR(45) NULL,
  `organism` VARCHAR(80) NULL,
  `create_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`, `user_id`),
  UNIQUE INDEX `id-sequence-info_UNIQUE` (`id` ASC),
  INDEX `fk_sequence-info_user_idx` (`user_id` ASC),
  CONSTRAINT `fk_sequence-info_user`
    FOREIGN KEY (`user_id`)
    REFERENCES `dnaa`.`user` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `dnaa`.`sequence_blocks`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `dnaa`.`sequence_blocks` (
  `id` INT NOT NULL,
  `sequence_info_id` INT UNSIGNED NOT NULL,
  `length` MEDIUMTEXT NOT NULL,
  `index` MEDIUMTEXT NOT NULL,
  `data` VARCHAR(4095) NOT NULL,
  PRIMARY KEY (`id`, `sequence_info_id`),
  INDEX `fk_sequence_blocks_sequence_info1_idx` (`sequence_info_id` ASC),
  CONSTRAINT `fk_sequence_blocks_sequence_info1`
    FOREIGN KEY (`sequence_info_id`)
    REFERENCES `dnaa`.`sequence_info` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `dnaa`.`account_type`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `dnaa`.`account_type` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(20) NOT NULL COMMENT 'name should be { LIMITED, STUDENT, EDUCATOR, SUBSCRIPTION }',
  `short_description` TEXT NULL,
  `long_description` TEXT NULL,
  `max_upload_size` INT NULL DEFAULT 0,
  `max_download_size` INT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `id_UNIQUE` (`id` ASC),
  UNIQUE INDEX `name_UNIQUE` (`name` ASC))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `dnaa`.`user_subscription`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `dnaa`.`user_subscription` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `user_id` INT UNSIGNED NOT NULL,
  `account_type_id` INT UNSIGNED NOT NULL,
  `create_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `expires_time` TIMESTAMP NULL,
  PRIMARY KEY (`id`, `user_id`, `account_type_id`),
  UNIQUE INDEX `id_UNIQUE` (`id` ASC),
  INDEX `fk_user_subscription_account_type1_idx` (`account_type_id` ASC),
  INDEX `fk_user_subscription_user1_idx` (`user_id` ASC),
  CONSTRAINT `fk_user_subscription_account_type1`
    FOREIGN KEY (`account_type_id`)
    REFERENCES `dnaa`.`account_type` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_user_subscription_user1`
    FOREIGN KEY (`user_id`)
    REFERENCES `dnaa`.`user` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
