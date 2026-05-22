const { MongoClient } = require('mongodb');

const URI = 'mongodb+srv://gamification_admin:A7kL9mXb@cluster0.xri2g8t.mongodb.net/gamificationdb?retryWrites=true&w=majority';

const badges = [
  {
    name: 'Primera Conexión',
    description: 'Realizaste tu primera conexión con otro usuario en la plataforma.',
    category: 'COMMON',
    xpReward: 10,
    iconUrl: 'https://cdn.example.com/monas/primera-conexion.png',
    active: true,
    createdAt: new Date(),
  },
  {
    name: 'Conector',
    description: 'Acumulaste 5 conexiones activas.',
    category: 'UNCOMMON',
    xpReward: 25,
    iconUrl: 'https://cdn.example.com/monas/conector.png',
    active: true,
    createdAt: new Date(),
  },
  {
    name: 'Embajador Social',
    description: 'Acumulaste 10 conexiones activas.',
    category: 'RARE',
    xpReward: 50,
    iconUrl: 'https://cdn.example.com/monas/embajador-social.png',
    active: true,
    createdAt: new Date(),
  },
  {
    name: 'Primer Parche',
    description: 'Te uniste o creaste tu primer parche.',
    category: 'COMMON',
    xpReward: 10,
    iconUrl: 'https://cdn.example.com/monas/primer-parche.png',
    active: true,
    createdAt: new Date(),
  },
  {
    name: 'Anfitrión',
    description: 'Creaste 2 parches como capitán.',
    category: 'UNCOMMON',
    xpReward: 25,
    iconUrl: 'https://cdn.example.com/monas/anfitrion.png',
    active: true,
    createdAt: new Date(),
  },
  {
    name: 'Planificador',
    description: 'Creaste un parche con más de 3 días de anticipación.',
    category: 'COMMON',
    xpReward: 15,
    iconUrl: 'https://cdn.example.com/monas/planificador.png',
    active: true,
    createdAt: new Date(),
  },
  {
    name: 'Explorador I',
    description: 'Visitaste 3 lugares distintos del campus.',
    category: 'COMMON',
    xpReward: 15,
    iconUrl: 'https://cdn.example.com/monas/explorador-i.png',
    active: true,
    createdAt: new Date(),
  },
  {
    name: 'Explorador II',
    description: 'Visitaste 5 lugares distintos del campus.',
    category: 'UNCOMMON',
    xpReward: 30,
    iconUrl: 'https://cdn.example.com/monas/explorador-ii.png',
    active: true,
    createdAt: new Date(),
  },
  {
    name: 'Asistente',
    description: 'Ingresaste un código alfanumérico válido de un evento universitario institucional.',
    category: 'RARE',
    xpReward: 50,
    iconUrl: 'https://cdn.example.com/monas/asistente.png',
    active: true,
    createdAt: new Date(),
  },
  {
    name: 'Primer Mensaje',
    description: 'Enviaste el primer mensaje en un parche recién creado.',
    category: 'COMMON',
    xpReward: 10,
    iconUrl: 'https://cdn.example.com/monas/primer-mensaje.png',
    active: true,
    createdAt: new Date(),
  },
  {
    name: 'Imán Social',
    description: 'Un nuevo usuario se unió a un parche que tú creaste.',
    category: 'COMMON',
    xpReward: 15,
    iconUrl: 'https://cdn.example.com/monas/iman-social.png',
    active: true,
    createdAt: new Date(),
  },
  {
    name: 'Meteoro Social',
    description: 'Pasaste de 0 a 10 conexiones en menos de 30 días desde tu registro.',
    category: 'EPIC',
    xpReward: 100,
    iconUrl: 'https://cdn.example.com/monas/meteoro-social.png',
    active: true,
    createdAt: new Date(),
  },
  {
    name: 'Coleccionista',
    description: 'Desbloqueaste las 12 monas anteriores. ¡Eres una Leyenda del Parche!',
    category: 'LEGENDARY',
    xpReward: 200,
    iconUrl: 'https://cdn.example.com/monas/coleccionista.png',
    active: true,
    createdAt: new Date(),
  },
];

async function seed() {
  const client = new MongoClient(URI);
  try {
    await client.connect();
    const col = client.db('gamificationdb').collection('badges');

    let inserted = 0;
    let skipped = 0;

    for (const badge of badges) {
      const exists = await col.findOne({ name: badge.name });
      if (exists) {
        console.log(`  SKIP  ${badge.name}`);
        skipped++;
      } else {
        await col.insertOne(badge);
        console.log(`  INSERT ${badge.name}`);
        inserted++;
      }
    }

    console.log(`\nDone. ${inserted} inserted, ${skipped} skipped.`);

    const all = await col.find({}, { projection: { name: 1, category: 1, _id: 1 } }).toArray();
    console.log('\nCatálogo actual:');
    all.forEach(b => console.log(`  ${b._id}  ${b.category.padEnd(10)}  ${b.name}`));
  } finally {
    await client.close();
  }
}

seed().catch(err => {
  console.error('Error:', err.message);
  process.exit(1);
});
