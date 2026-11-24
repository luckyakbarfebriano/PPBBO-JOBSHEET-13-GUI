-- Database Dummy Clone (PostgreSQL) untuk PICO (Panel Admin - Stand & Penjual)
-- Sesuai tampilan PBL Project (foto screenshot)
--
-- Catatan: Buat database terlebih dahulu di pgAdmin atau psql:
--   CREATE DATABASE db_pico_dummy;
-- Lalu koneksikan ke database db_pico_dummy dan jalankan script di bawah ini.

DROP TABLE IF EXISTS stand;

CREATE TABLE stand (
    id_stand SERIAL PRIMARY KEY,
    no_kios INT NOT NULL UNIQUE,
    nama_stand VARCHAR(100) NOT NULL,
    lokasi VARCHAR(100) DEFAULT '-',
    status VARCHAR(10) NOT NULL DEFAULT 'Aktif' CHECK (status IN ('Aktif', 'Tutup')),
    nama_penjual VARCHAR(100) DEFAULT '-',
    username_penjual VARCHAR(50) DEFAULT '-',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Data dummy sesuai foto tampilan Stand & Penjual PICO
INSERT INTO stand (no_kios, nama_stand, lokasi, status, nama_penjual, username_penjual) VALUES
(1,  'Kedai Bu Ima',          '-', 'Aktif', 'Bu Ima',         '@bu_ima'),
(3,  'Naqyka',                '-', 'Aktif', 'Penjual Naqyka', '@naqyka'),
(6,  'Bakso Pak Di',          '-', 'Aktif', 'Cak Di',         '@dika'),
(10, 'Bakso Supangat',        '-', 'Aktif', 'Supangat',       '@supangat'),
(11, 'Nanang Geprek',         '-', 'Aktif', 'Nanang',         '@nanang'),
(12, 'Warung Pecel Bu Sri',   '-', 'Aktif', 'Sri Wahyuni',    '@wahyuni'),
(13, 'Pempek Palembang Asli', '-', 'Aktif', 'Hendra Wijaya',  '@hendra'),
(14, 'Warung Maya',           '-', 'Aktif', 'Maya Lestari',   '@maya_lestari');
