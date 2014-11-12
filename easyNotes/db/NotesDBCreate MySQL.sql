—- Regular tables 

CREATE TABLE CATEGORY 
(
ID BIGINT NOT NULL, 
TITLE VARCHAR(255), 
CREATED TIMESTAMP, 
MODIFIED TIMESTAMP, 
PRIMARY KEY (ID)
);

CREATE TABLE NB_CATEGORY 
(
CATEGORY_ID BIGINT NOT NULL, 
NOTEBOOK_ID BIGINT NOT NULL, 
PRIMARY KEY (CATEGORY_ID, NOTEBOOK_ID)
);

CREATE TABLE NOTEBOOK 
(
ID BIGINT NOT NULL, 
TITLE VARCHAR(255), 
CREATED TIMESTAMP, 
NOTEBOOKTYPE INTEGER, 
MODIFIED TIMESTAMP, 
PRIMARY KEY (ID)
);

CREATE TABLE CARD 
(
ID BIGINT NOT NULL, 
TEXT VARCHAR(3000), 
BACKTEXT VARCHAR(255), 
NROFWRONGTOTAL INTEGER, 
LASTLEARNED TIMESTAMP NULL, 
ANSWER INTEGER, 
FRONTTEXT VARCHAR(255), 
NROFCORRECTTOTAL INTEGER, 
MODIFIED TIMESTAMP NOT NULL, 
NROFCORRECT INTEGER, 
NEXTSCHEDULED TIMESTAMP NULL, 
NROFWRONG INTEGER, 
NOTETYPE INTEGER, 
CREATED TIMESTAMP NOT NULL, 
COMPARTMENT INTEGER, 
NOTEBOOK_ID BIGINT, 
PRIMARY KEY (ID)
);

ALTER TABLE NB_CATEGORY ADD CONSTRAINT NBCTEGORYCTEGORYID FOREIGN KEY (CATEGORY_ID) REFERENCES CATEGORY (ID);
ALTER TABLE NB_CATEGORY ADD CONSTRAINT NBCTEGORYNTEBOOKID FOREIGN KEY (NOTEBOOK_ID) REFERENCES NOTEBOOK (ID);
ALTER TABLE CARD ADD CONSTRAINT CARD_NOTEBOOK_ID FOREIGN KEY (NOTEBOOK_ID) REFERENCES NOTEBOOK (ID);

CREATE TABLE SEQUENCE (SEQ_NAME VARCHAR(50) NOT NULL, SEQ_COUNT DECIMAL(15), PRIMARY KEY (SEQ_NAME));
INSERT INTO SEQUENCE(SEQ_NAME, SEQ_COUNT) values ('SEQ_GEN', 0);
