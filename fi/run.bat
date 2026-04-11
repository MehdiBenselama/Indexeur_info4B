@echo off
set SRC=src
set OUT=bin
echo ========================================
echo   FileIndexer - Compilation
echo ========================================
if not exist %OUT% mkdir %OUT%
javac -d %OUT% ^
    %SRC%\fileindexer\data\FicheDocument.java ^
    %SRC%\fileindexer\data\ResultatRecherche.java ^
    %SRC%\fileindexer\data\FileQueue.java ^
    %SRC%\fileindexer\data\IndexInverse.java ^
    %SRC%\fileindexer\engine\Explorateur.java ^
    %SRC%\fileindexer\engine\ThreadIndexeur.java ^
    %SRC%\fileindexer\engine\MoteurIndexation.java ^
    %SRC%\fileindexer\network\ServeurIndex.java ^
    %SRC%\fileindexer\network\ConnexionClient.java ^
    %SRC%\fileindexer\ui\ProgrammePrincipal.java
if %errorlevel% neq 0 (
    echo ERREUR de compilation !
    pause
    exit /b
)
echo Compilation reussie !
java -cp %OUT% fileindexer.ui.ProgrammePrincipal
pause
