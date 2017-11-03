--
-- PostgreSQL database dump
--

-- Dumped from database version 9.4.4
-- Dumped by pg_dump version 9.4.4
-- Started on 2017-11-03 10:15:10

SET statement_timeout = 0;
SET lock_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

--
-- TOC entry 174 (class 3079 OID 11855)
-- Name: plpgsql; Type: EXTENSION; Schema: -; Owner: 
--

CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;


--
-- TOC entry 2003 (class 0 OID 0)
-- Dependencies: 174
-- Name: EXTENSION plpgsql; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';


SET search_path = public, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- TOC entry 172 (class 1259 OID 26925)
-- Name: inbox; Type: TABLE; Schema: public; Owner: vjapp; Tablespace: 
--

CREATE TABLE inbox (
    owner text,
    uuid text,
    title character varying(256),
    message character varying(5000),
    date text,
    sender text
);


ALTER TABLE inbox OWNER TO vjapp;

--
-- TOC entry 173 (class 1259 OID 26931)
-- Name: users; Type: TABLE; Schema: public; Owner: vjapp; Tablespace: 
--

CREATE TABLE users (
    email text,
    password text,
    uuid text,
    status text,
    service text,
    sfrom text,
    suntil text,
    nama text
);


ALTER TABLE users OWNER TO vjapp;

--
-- TOC entry 1994 (class 0 OID 26925)
-- Dependencies: 172
-- Data for Name: inbox; Type: TABLE DATA; Schema: public; Owner: vjapp
--

COPY inbox (owner, uuid, title, message, date, sender) FROM stdin;
\.


--
-- TOC entry 1995 (class 0 OID 26931)
-- Dependencies: 173
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: vjapp
--

COPY users (email, password, uuid, status, service, sfrom, suntil, nama) FROM stdin;
faisal@visijurusan.com	tikobuas69	777	admin	admin	0	0	Faisal Magrie
sabda@gmail.com	abcde	111111	active	gold	0	0	Sabda PS
pio@gmail.com	abcde	222222	active	platinum	0	0	Hario Iman Setyo
donna@gmail.com	abcde	333333	active	gold	0	0	Donna Safiera
wilona@gmail.com	abcde	444444	active	platinum	0	0	Wilona Arieta
calvin@gmail.com	abcde	555555	active	gold	0	0	Calvin Irwan
\.


--
-- TOC entry 2002 (class 0 OID 0)
-- Dependencies: 5
-- Name: public; Type: ACL; Schema: -; Owner: postgres
--

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM postgres;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;


-- Completed on 2017-11-03 10:15:10

--
-- PostgreSQL database dump complete
--

