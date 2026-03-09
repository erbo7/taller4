package interfaces;

import java.util.List;

/**
 * Interface genérica para operaciones CRUD
 * Sistema Veterinario - DiazPet
 * @param <T> Tipo de entidad (Cliente, Mascota, Cita, etc.)
 */
public interface CRUD<T> {
    
    /**
     * Método para listar todos los registros
     * @return List<T> lista de entidades
     */
    public List<T> listar();
    
    /**
     * Método para agregar un nuevo registro
     * @param objeto entidad a agregar
     * @return int 1 si se agregó correctamente, 0 si falló
     */
    public int agregar(T objeto);
    
    /**
     * Método para actualizar un registro existente
     * @param objeto entidad con los datos actualizados
     * @return int 1 si se actualizó correctamente, 0 si falló
     */
    public int actualizar(T objeto);
    
    /**
     * Método para eliminar/anular un registro
     * @param id identificador del registro
     * @return int 1 si se eliminó correctamente, 0 si falló
     */
    public int eliminar(int id);
    
    /**
     * Método para buscar un registro por ID
     * @param id identificador del registro
     * @return T objeto encontrado o null
     */
    public T buscarPorId(int id);
}