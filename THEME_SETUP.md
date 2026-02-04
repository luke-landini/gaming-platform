# 🎨 Gaming Platform - Tema Dark con Arancione

## ✅ Tema Installato

Ho creato un tema custom **"gaming-dark"** con design moderno, dark e minimale usando i colori **nero e arancione**.

---

## 🎯 **COME ATTIVARE IL TEMA IN KEYCLOAK**

### **STEP 1: Vai su Keycloak Admin Console**
1. Apri [http://localhost:8080](http://localhost:8080)
2. Login con `admin` / `admin`

### **STEP 2: Seleziona il tuo realm**
- Clicca sul menu a tendina in alto a sinistra
- Seleziona `myrealm`

### **STEP 3: Applica il tema**
1. Vai su **Realm Settings** (nel menu a sinistra)
2. Clicca sulla tab **Themes**
3. Nella sezione **Login theme**, seleziona **`gaming-dark`** dal menu a tendina
4. Clicca **Save**

### **STEP 4: Abilita la registrazione (opzionale)**
1. Sempre in **Realm Settings**, vai sulla tab **Login**
2. Abilita l'opzione **User registration** (ON)
3. Salva

---

## 🎨 **DESIGN APPLICATO**

### **Colori principali:**
- **Background primario:** `#0a0a0a` (nero profondo)
- **Background card:** `#1a1a1a` (nero chiaro)
- **Arancione primario:** `#ff6b35`
- **Arancione hover:** `#ff8555`
- **Testo primario:** `#ffffff`
- **Testo secondario:** `#b0b0b0`
- **Bordi:** `#333333`

### **Caratteristiche del design:**
✅ Gradiente animato sullo sfondo  
✅ Bordo arancione animato in alto alle card  
✅ Animazioni fluide e moderne  
✅ Effetti hover con glow arancione  
✅ Bottoni con animazione shimmer  
✅ Form inputs con focus arancione  
✅ Dark mode perfetto per gaming  

---

## 🖥️ **FRONTEND AGGIORNATO**

Ho aggiornato anche il frontend con lo stesso tema:

### **Componenti aggiornati:**
- ✅ **App.css** - Background e animazioni globali
- ✅ **UserProfile.css** - Profilo utente con tema dark/arancione
- ✅ **App.jsx** - Welcome screen con design moderno

### **Pagine stilizzate:**
- **Welcome/Login page** - Design accogliente con gradiente e animazioni
- **User Profile** - Card moderne con bordi arancioni e effetti glow
- **Loading states** - Spinner arancione con animazioni fluide

---

## 🚀 **COME TESTARE**

1. **Vai su** [http://localhost:5173](http://localhost:5173)
2. **Clicca su "Login with Keycloak"**
3. **Verrai reindirizzato** alla pagina di login di Keycloak con il nuovo tema dark/arancione
4. **Dopo il login**, vedrai il profilo utente con lo stesso design

---

## 📸 **PREVIEW DEL TEMA**

### **Login Page (Keycloak):**
- Background nero con gradiente radiale arancione
- Card centrale con bordo arancione in alto
- Input fields con bordo arancione al focus
- Bottone arancione con animazione shimmer
- Link arancioni con underline animato

### **Welcome Page (Frontend):**
- Background nero con gradiente animato
- Card centrale con titolo gradient (bianco → arancione)
- Bottone "Login" arancione con effetti hover

### **User Profile Page:**
- Header con titolo gradient
- Banner con gradiente arancione
- Avatar con bordo arancione
- Card dettagli con hover arancione
- Bottoni styled in tema arancione

---

## 🔧 **PERSONALIZZAZIONE AVANZATA**

Se vuoi modificare ulteriormente il tema, edita questi file:

```
infra/keycloak-theme/gaming-dark/
├── theme.properties
└── login/
    └── resources/
        └── css/
            └── gaming-dark.css  ← Modifica qui gli stili
```

Dopo le modifiche, riavvia Keycloak:
```bash
docker restart gaming-keycloak
```

---

## 📝 **NOTE**

- Il tema è completamente responsive
- Supporta dark mode nativo
- Ottimizzato per gaming e esports
- Design professionale e moderno
- Accessibile e user-friendly

---

**Buon gaming! 🎮🔥**
