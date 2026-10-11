# Notes de version — Oracle de Marie 1.3.2 (versionCode 6)

## FR (à coller dans Play Console — Notes de version FR)

Oracle de Marie vous accompagne chaque jour avec :
✦ Un tirage quotidien de carte guidée par 5 thèmes de vie
✦ Un tirage à 3 cartes (Corps, Âme, Esprit)
✦ 14 prières à Marie, avec minuteur de méditation
✦ Des paroles inspirantes de Marie
✦ Un journal de vos tirages

Cette version soigne l'écoute des prières : toutes les voix ont été ré-enregistrées avec un volume homogène, les fonds sonores ont été nettoyés et la cloche de fin de méditation adoucie. Des silences et des fondus ont été ajoutés entre la prière, la musique et la cloche, pour un moment plus paisible. Les rappels quotidiens gagnent aussi en fiabilité.

## EN (à coller dans Play Console — Release notes EN)

Oracle de Marie guides you daily with:
✦ A daily card draw guided by 5 life themes
✦ A 3-card draw (Body, Soul, Spirit)
✦ 14 prayers to Mary, with a meditation timer
✦ Inspiring words from Mary
✦ A journal of your draws

This version improves the prayer listening experience: all voices have been re-recorded at an even volume, the ambient sounds have been cleaned up, and the closing meditation bell softened. Gentle pauses and fades now separate the prayer, the music and the bell for a more peaceful moment. Daily reminders are also more reliable.

---

## Détail technique (non destiné au Play Store)

- 14 prières ré-enregistrées, niveaux égalisés
- `gong.mp3` remplacé par `cloche.ogg`, volume abaissé
- `bruit_eau.ogg` ré-enregistré (parasites supprimés), `chant_oiseau.ogg` réencodé (7,9 → 4,2 Mo)
- 4 s de silence entre la fin de la prière et le fond sonore
- 5 s de silence entre la fin du minuteur et la cloche
- Fondu d'ouverture 3 s / de fermeture 4 s sur le fond sonore
- Rappels quotidiens : WorkManager remplacé par AlarmManager (fiabilité app fermée)
- Messages de rappel externalisés en FR + EN
- Poids du bundle : 33,7 Mo → 28 Mo
