CREATE DATABASE IF NOT EXISTS pharma_connect;
USE pharma_connect;
-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Hôte : 127.0.0.1
-- Généré le : mar. 19 mai 2026 à 19:20
-- Version du serveur : 10.4.32-MariaDB
-- Version de PHP : 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de données : `pharma_connect`
--

-- --------------------------------------------------------

--
-- Structure de la table `administrateur`
--

CREATE TABLE `administrateur` (
  `id` int(11) NOT NULL,
  `niveau_acces` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Structure de la table `ligne_prescription`
--

CREATE TABLE `ligne_prescription` (
  `id` int(11) NOT NULL,
  `ordonnance_id` int(11) DEFAULT NULL,
  `medicament_id` int(11) DEFAULT NULL,
  `posologie` varchar(100) DEFAULT NULL,
  `duree` int(11) DEFAULT NULL,
  `quantite` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Structure de la table `medicament`
--

CREATE TABLE `medicament` (
  `id` int(11) NOT NULL,
  `nom` varchar(100) DEFAULT NULL,
  `categorie` varchar(100) DEFAULT NULL,
  `prix_unitaire` decimal(10,2) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Structure de la table `notification`
--

CREATE TABLE `notification` (
  `id` int(11) NOT NULL,
  `utilisateur_id` int(11) DEFAULT NULL,
  `message` text DEFAULT NULL,
  `type` varchar(50) DEFAULT NULL,
  `date_envoi` datetime DEFAULT NULL,
  `is_read` tinyint(1) DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Structure de la table `ordonnance`
--

CREATE TABLE `ordonnance` (
  `id` int(11) NOT NULL,
  `patient_id` int(11) DEFAULT NULL,
  `date_emission` date DEFAULT NULL,
  `date_expiration` date DEFAULT NULL,
  `statut` enum('EN_ATTENTE','VALIDEE','REFUSEE') DEFAULT NULL,
  `signature_numerique` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Structure de la table `patient`
--

CREATE TABLE `patient` (
  `id` int(11) NOT NULL,
  `date_naissance` date DEFAULT NULL,
  `adresse` text DEFAULT NULL,
  `telephone` varchar(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Structure de la table `pharmacie`
--

CREATE TABLE `pharmacie` (
  `id` int(11) NOT NULL,
  `nom` varchar(100) DEFAULT NULL,
  `adresse` text DEFAULT NULL,
  `horaires` text DEFAULT NULL,
  `telephone` varchar(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Structure de la table `pharmacien`
--

CREATE TABLE `pharmacien` (
  `id` int(11) NOT NULL,
  `diplome` varchar(100) DEFAULT NULL,
  `pharmacie_id` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Structure de la table `stock`
--

CREATE TABLE `stock` (
  `id` int(11) NOT NULL,
  `medicament_id` int(11) DEFAULT NULL,
  `pharmacie_id` int(11) DEFAULT NULL,
  `quantite_disponible` int(11) DEFAULT NULL,
  `date_expiration` date DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Structure de la table `utilisateur`
--

CREATE TABLE `utilisateur` (
  `id` int(11) NOT NULL,
  `nom` varchar(100) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `mot_de_passe` varchar(255) DEFAULT NULL,
  `role` enum('PATIENT','PHARMACIEN','ADMIN') DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Index pour les tables déchargées
--

--
-- Index pour la table `administrateur`
--
ALTER TABLE `administrateur`
  ADD PRIMARY KEY (`id`);

--
-- Index pour la table `ligne_prescription`
--
ALTER TABLE `ligne_prescription`
  ADD PRIMARY KEY (`id`),
  ADD KEY `ordonnance_id` (`ordonnance_id`),
  ADD KEY `medicament_id` (`medicament_id`);

--
-- Index pour la table `medicament`
--
ALTER TABLE `medicament`
  ADD PRIMARY KEY (`id`);

--
-- Index pour la table `notification`
--
ALTER TABLE `notification`
  ADD PRIMARY KEY (`id`),
  ADD KEY `utilisateur_id` (`utilisateur_id`);

--
-- Index pour la table `ordonnance`
--
ALTER TABLE `ordonnance`
  ADD PRIMARY KEY (`id`),
  ADD KEY `patient_id` (`patient_id`);

--
-- Index pour la table `patient`
--
ALTER TABLE `patient`
  ADD PRIMARY KEY (`id`);

--
-- Index pour la table `pharmacie`
--
ALTER TABLE `pharmacie`
  ADD PRIMARY KEY (`id`);

--
-- Index pour la table `pharmacien`
--
ALTER TABLE `pharmacien`
  ADD PRIMARY KEY (`id`),
  ADD KEY `pharmacie_id` (`pharmacie_id`);

--
-- Index pour la table `stock`
--
ALTER TABLE `stock`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `medicament_id` (`medicament_id`,`pharmacie_id`),
  ADD KEY `pharmacie_id` (`pharmacie_id`);

--
-- Index pour la table `utilisateur`
--
ALTER TABLE `utilisateur`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `email` (`email`);

--
-- AUTO_INCREMENT pour les tables déchargées
--

--
-- AUTO_INCREMENT pour la table `ligne_prescription`
--
ALTER TABLE `ligne_prescription`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `medicament`
--
ALTER TABLE `medicament`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `notification`
--
ALTER TABLE `notification`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `ordonnance`
--
ALTER TABLE `ordonnance`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `pharmacie`
--
ALTER TABLE `pharmacie`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `stock`
--
ALTER TABLE `stock`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `utilisateur`
--
ALTER TABLE `utilisateur`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- Contraintes pour les tables déchargées
--

--
-- Contraintes pour la table `administrateur`
--
ALTER TABLE `administrateur`
  ADD CONSTRAINT `administrateur_ibfk_1` FOREIGN KEY (`id`) REFERENCES `utilisateur` (`id`) ON DELETE CASCADE;

--
-- Contraintes pour la table `ligne_prescription`
--
ALTER TABLE `ligne_prescription`
  ADD CONSTRAINT `ligne_prescription_ibfk_1` FOREIGN KEY (`ordonnance_id`) REFERENCES `ordonnance` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `ligne_prescription_ibfk_2` FOREIGN KEY (`medicament_id`) REFERENCES `medicament` (`id`);

--
-- Contraintes pour la table `notification`
--
ALTER TABLE `notification`
  ADD CONSTRAINT `notification_ibfk_1` FOREIGN KEY (`utilisateur_id`) REFERENCES `utilisateur` (`id`);

--
-- Contraintes pour la table `ordonnance`
--
ALTER TABLE `ordonnance`
  ADD CONSTRAINT `ordonnance_ibfk_1` FOREIGN KEY (`patient_id`) REFERENCES `patient` (`id`);

--
-- Contraintes pour la table `patient`
--
ALTER TABLE `patient`
  ADD CONSTRAINT `patient_ibfk_1` FOREIGN KEY (`id`) REFERENCES `utilisateur` (`id`) ON DELETE CASCADE;

--
-- Contraintes pour la table `pharmacien`
--
ALTER TABLE `pharmacien`
  ADD CONSTRAINT `pharmacien_ibfk_1` FOREIGN KEY (`id`) REFERENCES `utilisateur` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `pharmacien_ibfk_2` FOREIGN KEY (`pharmacie_id`) REFERENCES `pharmacie` (`id`);

--
-- Contraintes pour la table `stock`
--
ALTER TABLE `stock`
  ADD CONSTRAINT `stock_ibfk_1` FOREIGN KEY (`medicament_id`) REFERENCES `medicament` (`id`),
  ADD CONSTRAINT `stock_ibfk_2` FOREIGN KEY (`pharmacie_id`) REFERENCES `pharmacie` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
