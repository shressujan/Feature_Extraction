-- CREATE DATABASE FOR /home/centos/orm_synthesizer/models/flagship/ImplSolution/flagship_Sol_251.sql

USE flagship_Sol_251;
--
-- Table structure for table Group
--

CREATE TABLE `Group` (
`groupName` varchar(64),
`groupID` int NOT NULL, 
PRIMARY KEY (`groupID`)
);

--
-- Table structure for table User
--

CREATE TABLE `User` (
`userName` varchar(64),
`userID` int NOT NULL, 
PRIMARY KEY (`userID`)
);

--
-- Table structure for table Category
--

CREATE TABLE `Category` (
`categoryName` varchar(64),
`groupID` int,
`categoryID` int NOT NULL, 
KEY `FK_Category_groupID_idx` (`groupID`),
PRIMARY KEY (`categoryID`)
);

--
-- Table structure for table Background
--

CREATE TABLE `Background` (
`backgroundName` varchar(64),
`backgroundID` int NOT NULL, 
PRIMARY KEY (`backgroundID`)
);

--
-- Table structure for table GroupUserAssociation
--

CREATE TABLE `GroupUserAssociation` (
`groupID` int NOT NULL, 
`userID` int NOT NULL, 
KEY `FK_GroupUserAssociation_groupID_idx` (`groupID`),
KEY `FK_GroupUserAssociation_userID_idx` (`userID`),
PRIMARY KEY (`groupID`,`userID`)
);

--
-- Table structure for table DocumentCategoryAssociation
--

CREATE TABLE `DocumentCategoryAssociation` (
`categoryID` int NOT NULL, 
`documentID` int NOT NULL, 
KEY `FK_DocumentCategoryAssociation_categoryID_idx` (`categoryID`),
KEY `FK_DocumentCategoryAssociation_documentID_idx` (`documentID`),
PRIMARY KEY (`categoryID`,`documentID`)
);

--
-- Table structure for table CategoryUserAssociation
--

CREATE TABLE `CategoryUserAssociation` (
`categoryID` int NOT NULL, 
`userID` int NOT NULL, 
KEY `FK_CategoryUserAssociation_categoryID_idx` (`categoryID`),
KEY `FK_CategoryUserAssociation_userID_idx` (`userID`),
PRIMARY KEY (`categoryID`,`userID`)
);

--
-- Table structure for table RevisionDocumentAssociation
--

CREATE TABLE `RevisionDocumentAssociation` (
`revisionID` int NOT NULL, 
`documentID` int NOT NULL, 
KEY `FK_RevisionDocumentAssociation_revisionID_idx` (`revisionID`),
KEY `FK_RevisionDocumentAssociation_documentID_idx` (`documentID`),
PRIMARY KEY (`revisionID`,`documentID`)
);

--
-- Table structure for table Document
--

CREATE TABLE `Document` (
`title` varchar(64),
`documentID` int NOT NULL, 
`userID` int,
KEY `FK_Document_userID_idx` (`userID`),
PRIMARY KEY (`documentID`)
);

--
-- Table structure for table RevisionUserAssociation
--

CREATE TABLE `RevisionUserAssociation` (
`revisionID` int NOT NULL, 
`userID` int NOT NULL, 
KEY `FK_RevisionUserAssociation_revisionID_idx` (`revisionID`),
KEY `FK_RevisionUserAssociation_userID_idx` (`userID`),
PRIMARY KEY (`revisionID`,`userID`)
);

--
-- Table structure for table Revision
--

CREATE TABLE `Revision` (
`revisionName` varchar(64),
`revisionID` int NOT NULL, 
PRIMARY KEY (`revisionID`)
);

--
-- Table structure for table CategoryBackgroundAssociation
--

CREATE TABLE `CategoryBackgroundAssociation` (
`categoryID` int NOT NULL, 
`backgroundID` int NOT NULL, 
KEY `FK_CategoryBackgroundAssociation_categoryID_idx` (`categoryID`),
KEY `FK_CategoryBackgroundAssociation_backgroundID_idx` (`backgroundID`),
PRIMARY KEY (`categoryID`,`backgroundID`)
);

ALTER TABLE `Category`
  ADD CONSTRAINT `FK_Category_groupID` FOREIGN KEY (`groupID`) REFERENCES `Group` (`groupID`) ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE `GroupUserAssociation`
  ADD CONSTRAINT `FK_GroupUserAssociation_groupID` FOREIGN KEY (`groupID`) REFERENCES `Group` (`groupID`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `FK_GroupUserAssociation_userID` FOREIGN KEY (`userID`) REFERENCES `User` (`userID`) ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE `DocumentCategoryAssociation`
  ADD CONSTRAINT `FK_DocumentCategoryAssociation_categoryID` FOREIGN KEY (`categoryID`) REFERENCES `Category` (`categoryID`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `FK_DocumentCategoryAssociation_documentID` FOREIGN KEY (`documentID`) REFERENCES `Document` (`documentID`) ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE `CategoryUserAssociation`
  ADD CONSTRAINT `FK_CategoryUserAssociation_categoryID` FOREIGN KEY (`categoryID`) REFERENCES `Category` (`categoryID`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `FK_CategoryUserAssociation_userID` FOREIGN KEY (`userID`) REFERENCES `User` (`userID`) ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE `RevisionDocumentAssociation`
  ADD CONSTRAINT `FK_RevisionDocumentAssociation_revisionID` FOREIGN KEY (`revisionID`) REFERENCES `Revision` (`revisionID`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `FK_RevisionDocumentAssociation_documentID` FOREIGN KEY (`documentID`) REFERENCES `Document` (`documentID`) ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE `Document`
  ADD CONSTRAINT `FK_Document_userID` FOREIGN KEY (`userID`) REFERENCES `User` (`userID`) ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE `RevisionUserAssociation`
  ADD CONSTRAINT `FK_RevisionUserAssociation_revisionID` FOREIGN KEY (`revisionID`) REFERENCES `Revision` (`revisionID`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `FK_RevisionUserAssociation_userID` FOREIGN KEY (`userID`) REFERENCES `User` (`userID`) ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE `CategoryBackgroundAssociation`
  ADD CONSTRAINT `FK_CategoryBackgroundAssociation_categoryID` FOREIGN KEY (`categoryID`) REFERENCES `Category` (`categoryID`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `FK_CategoryBackgroundAssociation_backgroundID` FOREIGN KEY (`backgroundID`) REFERENCES `Background` (`backgroundID`) ON DELETE CASCADE ON UPDATE CASCADE;

