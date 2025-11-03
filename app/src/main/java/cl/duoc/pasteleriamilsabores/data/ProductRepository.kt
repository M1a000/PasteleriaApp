package cl.duoc.pasteleriamilsabores.data

import cl.duoc.pasteleriamilsabores.R

object ProductRepository {

    fun getAllProducts(): List<Product> {
        return listOf(
            Product(
                id = "TC001",
                category = "Tortas Cuadradas",
                name = "Torta Cuadrada de Chocolate",
                price = 45000,
                description = "Deliciosa torta de chocolate con capas de ganache y un toque de avellanas. Personalizable con mensajes especiales.",
                image = R.drawable.torta_cuadrada_chocolate
            ),
            Product(
                id = "TC002",
                category = "Tortas Cuadradas",
                name = "Torta Cuadrada de Frutas",
                price = 50000,
                description = "Una mezcla de frutas frescas y crema chantilly sobre un suave bizcocho de vainilla, ideal para celebraciones.",
                image = R.drawable.torta_cuadrada_frutas
            ),
            Product(
                id = "TT001",
                category = "Tortas Circulares",
                name = "Torta Circular de Vainilla",
                price = 40000,
                description = "Bizcocho de vainilla clásico relleno con crema pastelera y cubierto con un glaseado dulce, perfecto para cualquier ocasión.",
                image = R.drawable.torta_circular_vainilla
            ),
            Product(
                id = "TT002",
                category = "Tortas Circulares",
                name = "Torta Circular de Manjar",
                price = 42000,
                description = "Torta tradicional chilena con manjar y nueces, un deleite para los amantes de los sabores dulces y clásicos.",
                image = R.drawable.torta_circular_manjar
            ),
            Product(
                id = "PI001",
                category = "Postres Individuales",
                name = "Mousse de Chocolate",
                price = 5000,
                description = "Postre individual cremoso y suave, hecho con chocolate de alta calidad, ideal para los amantes del chocolate.",
                image = R.drawable.mousse_de_chocolate
            ),
            Product(
                id = "PI002",
                category = "Postres Individuales",
                name = "Tiramisú Clásico",
                price = 5500,
                description = "Un postre italiano individual con capas de café, mascarpone y cacao, perfecto para finalizar cualquier comida.",
                image = R.drawable.tiramisu_clasico
            ),
            Product(
                id = "PSA001",
                category = "Productos Sin Azúcar",
                name = "Torta Sin Azúcar de Naranja",
                price = 48000,
                description = "Torta ligera y deliciosa, endulzada naturalmente, ideal para quienes buscan opciones más saludables."
            ),
            Product(
                id = "PSA002",
                category = "Productos Sin Azúcar",
                name = "Cheesecake Sin Azúcar",
                price = 47000,
                description = "Suave y cremoso, este cheesecake es una opción perfecta para disfrutar sin culpa."
            ),
            Product(
                id = "PT001",
                category = "Pastelería Tradicional",
                name = "Empanada de Manzana",
                price = 3000,
                description = "Pastelería tradicional rellena de manzanas especiadas, perfecta para un dulce desayuno o merienda."
            ),
            Product(
                id = "PT002",
                category = "Pastelería Tradicional",
                name = "Tarta de Santiago",
                price = 6000,
                description = "Tradicional tarta española hecha con almendras, azúcar, y huevos, una delicia para los amantes de los postres clásicos."
            ),
            Product(
                id = "PG001",
                category = "Productos Sin Gluten",
                name = "Brownie Sin Gluten",
                price = 4000,
                description = "Rico y denso, este brownie es perfecto para quienes necesitan evitar el gluten sin sacrificar el sabor."
            ),
            Product(
                id = "PG002",
                category = "Productos Sin Gluten",
                name = "Pan Sin Gluten",
                price = 3500,
                description = "Suave y esponjoso, ideal para sandwiches o para acompañar cualquier comida."
            ),
            Product(
                id = "PV001",
                category = "Productos Vegana",
                name = "Torta Vegana de Chocolate",
                price = 50000,
                description = "Torta de chocolate húmeda y deliciosa, hecha sin productos de origen animal, perfecta para veganos."
            ),
            Product(
                id = "PV002",
                category = "Productos Vegana",
                name = "Galletas Veganas de Avena",
                price = 4500,
                description = "Crujientes y sabrosas, estas galletas son una excelente opción para un snack saludable y vegano."
            ),
            Product(
                id = "TE001",
                category = "Tortas Especiales",
                name = "Torta Especial de Cumpleaños",
                price = 55000,
                description = "Diseñada especialmente para celebraciones, personalizable con decoraciones y mensajes únicos."
            ),
            Product(
                id = "TE002",
                category = "Tortas Especiales",
                name = "Torta Especial de Boda",
                price = 60000,
                description = "Elegante y deliciosa, esta torta está diseñada para ser el centro de atención en cualquier boda."
            )
        )
    }
}