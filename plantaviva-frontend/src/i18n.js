import i18n from 'i18next'
import { initReactI18next } from 'react-i18next'
import es from './locales/es.json'
import en from './locales/en.json'

// Configuración de i18next
i18n
  .use(initReactI18next) // Conecta i18next con React
  .init({
    resources: {
      es: { translation: es },
      en: { translation: en },
    },
    lng: localStorage.getItem('language') || 'es', // Idioma por defecto: español
    fallbackLng: 'es', // Si falla, usar español
    interpolation: {
      escapeValue: false, // React ya escapa los valores
    },
  })

export default i18n