# Cosmic Skill Collector

![Java](https://img.shields.io/badge/language-Java-orange.svg)
![Platform](https://img.shields.io/badge/platform-Swing%20GUI-blue.svg)
![Build](https://img.shields.io/badge/build-Maven-red.svg)
![Database](https://img.shields.io/badge/database-SQLite-blue.svg)

**Cosmic Skill Collector** adalah game 2D berbasis Java Swing yang menantang pemain untuk mengendalikan seorang astronot dalam misi mengumpulkan "Skill Balls" di luar angkasa. Dengan *gameplay* yang dinamis, sistem skor, dan leaderboard yang persisten, game ini dibangun untuk menguji refleks dan ketangkasan pemain.

Proyek ini merupakan implementasi dari Tugas Masa Depan Individu untuk mata kuliah Desain dan Pemrograman Berorientasi Objek, yang didesain menggunakan arsitektur **MVVM (Model-View-ViewModel)** dan dikelola menggunakan **Maven**.

[Gameplay Screenshot]
![Image](https://github.com/user-attachments/assets/ca235d74-abcc-4be4-a529-3526f6e04911)

![Image](https://github.com/user-attachments/assets/a45f5f23-df18-4768-bf9f-dc8714898f15)

## Daftar Isi
- [Fitur Utama](#fitur-utama)
- [Arsitektur Proyek](#arsitektur-proyek)
- [Teknologi yang Digunakan](#teknologi-yang-digunakan)
- [Struktur Proyek](#struktur-proyek)
- [Panduan Instalasi & Menjalankan](#panduan-instalasi--menjalankan)
- [Cara Bermain](#cara-bermain)
- [Kredit dan Atribusi](#kredit-dan-atribusi)
- [Lisensi](#lisensi)

## Fitur Utama
- **Gameplay Dinamis**: Objek "Skill Ball" muncul secara acak dari berbagai arah dengan nilai dan kecepatan yang berbeda-beda.
- **Sistem Skor & Peringatan**: Kumpulkan bola untuk mendapatkan skor. Hindari objek berbahaya (Pluto) yang akan menambah jumlah peringatan. Game berakhir jika batas peringatan tercapai.
- **Leaderboard Persisten**: Skor tertinggi disimpan secara lokal menggunakan database SQLite, memungkinkan pemain untuk bersaing memperebutkan peringkat teratas di "Hall of Fame".
- **Kontrol Responsif**:
    - Gerakkan pemain menggunakan tombol `W, A, S, D` atau `Tombol Panah`.
    - Tangkap bola dengan `Klik Kiri Mouse`.
- **Antarmuka Modern**: UI yang dirancang dengan efek visual seperti *glass morphism*, gradien, dan animasi untuk memberikan pengalaman yang menarik.
- **Efek Suara & Musik Latar**: Dilengkapi dengan musik latar yang memacu adrenalin dan efek suara untuk setiap aksi penting dalam game.
- **Arsitektur MVVM**: Kode diorganisir dengan rapi menggunakan pola desain MVVM untuk memisahkan logika data, tampilan, dan logika bisnis.

## Arsitektur Proyek
Proyek ini mengadopsi pola arsitektur **Model-View-ViewModel (MVVM)** untuk memastikan kode yang bersih, terorganisir, dan mudah dikelola.

- **Model**: Berisi kelas-kelas data murni yang merepresentasikan objek dalam game.
  - `Ball.java`, `PlayerResult.java`, `FloatingText.java`, `Net.java`
- **View**: Bertanggung jawab untuk menampilkan antarmuka pengguna (UI) dan menerima input dari pengguna. View "mengamati" perubahan pada ViewModel.
  - `MainMenuView.java`, `GameView.java`
- **ViewModel**: Bertindak sebagai jembatan antara Model dan View. ViewModel berisi semua logika tampilan dan *state management*, serta memberi tahu View kapan harus memperbarui dirinya.
  - `MainMenuViewModel.java`, `GameViewModel.java`
- **Repository**: Bertugas sebagai abstraksi untuk sumber data. Dalam proyek ini, ia mengelola semua operasi ke database SQLite.
  - `DatabaseRepository.java`
- **Service**: Menyediakan fungsionalitas spesifik yang dapat digunakan di seluruh aplikasi, seperti manajemen suara.
  - `SoundService.java`

## Teknologi yang Digunakan
- **Bahasa**: [Java](https://www.java.com/)
- **Framework GUI**: [Java Swing](https://docs.oracle.com/javase/tutorial/uiswing/)
- **Build & Dependency Management**: [Apache Maven](https://maven.apache.org/)
- **Database**: [SQLite](https://www.sqlite.org/) via [sqlite-jdbc](https://github.com/xerial/sqlite-jdbc)

## Struktur Proyek
Proyek ini menggunakan struktur standar Maven untuk memudahkan pengelolaan.
```
TMDDPBO2025C2/
├── pom.xml                 (File konfigurasi Maven)
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── models/
│   │   │   ├── repositories/
│   │   │   ├── services/
│   │   │   ├── viewmodels/
│   │   │   ├── views/
│   │   │   └── Main.java   (Entry point aplikasi)
│   │   └── resources/
│   │       ├── database/
│   │       │   └── skillballs.db
│   │       ├── images/
│   │       └── sounds/
└── README.md
```

## Panduan Instalasi & Menjalankan

### Prasyarat
1.  **JDK (Java Development Kit)**: Pastikan Anda memiliki JDK versi 11 atau yang lebih baru.
2.  **Maven**: Pastikan [Apache Maven](https://maven.apache.org/download.cgi) terinstal dan terkonfigurasi di sistem Anda.
3.  **Git**: Diperlukan untuk men-clone repositori.

### Langkah-Langkah
1.  **Clone Repositori**
    ```sh
    git clone [https://github.com/Tarz24/TMDDPBO2025C2.git](https://github.com/Tarz24/TMDDPBO2025C2.git)
    cd TMDDPBO2025C2
    ```
2.  **Build Proyek dengan Maven**
    Buka terminal atau command prompt di direktori root proyek, lalu jalankan perintah berikut. Perintah ini akan mengunduh semua dependensi yang dibutuhkan (seperti driver SQLite) dan meng-compile kode sumber.
    ```sh
    mvn clean install
    ```
3.  **Jalankan Aplikasi**
    Setelah proses build berhasil, jalankan aplikasi menggunakan perintah Maven:
    ```sh
    mvn exec:java -Dexec.mainClass="Main"
    ```
    Atau, Anda bisa menjalankan file JAR yang telah dibuat di dalam folder `target/`.

## Cara Bermain
1.  **Masukkan Nama**: Pada menu utama, ketik nama pengguna (ID Komandan) Anda di kolom yang tersedia.
2.  **Mulai Misi**: Klik tombol `🚀 LAUNCH MISSION` untuk memulai permainan.
3.  **Navigasi**: Gunakan tombol `W, A, S, D` atau `Tombol Panah` untuk menggerakkan astronot Anda di area permainan.
4.  **Tangkap Bola**: Arahkan kursor mouse ke objek yang melayang dan **klik kiri** untuk menembakkan jaring dan menangkapnya.
5.  **Kumpulkan Skor**:
    - Setiap objek memiliki nilai yang berbeda.
    - Objek **Pluto** bernilai negatif dan akan menambah `PLUTO HITS`.
6.  **Hindari Bahaya**: Permainan akan berakhir jika Anda mengenai Pluto sebanyak **30 kali**.
7.  **Selesaikan Misi**: Anda bisa menekan tombol `SPACE` untuk mengakhiri permainan kapan saja dan menyimpan skor Anda.

## Kredit dan Atribusi
Semua aset visual dan audio yang digunakan dalam proyek ini adalah milik kreator aslinya. Terima kasih kepada:

#### 🎨 Aset Visual
- **Background**: Pinterest Space Collection
- **Star, Net, Space-Shuttle, UFO**: Freepik - Flaticon
- **Asteroid**: VectorPortal - Flaticon
- **Astronaut**: iconfield - Flaticon
- **Meteorite**: Vitaly Gorbachev - Flaticon
- **Pluto**: Peerapak Takpho - Flaticon
- **Satellite**: Muhammad Atif - Flaticon

#### 🎵 Aset Audio
- **Catch Sound Effect**: Pixabay - Collect Points
- **Background Music**: Retrowave by Walen (freetouse.com)

---

Dibuat oleh **Muhammad Akhtar Rizki Ramadha** - 2025
