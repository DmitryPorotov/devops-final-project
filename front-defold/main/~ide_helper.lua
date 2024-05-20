---@class hash: userdata

---@class node: userdata

---@class constant: userdata

---@class url: userdata

---@class b2Body: userdata

---@class b2World: userdata

---@class b2BodyType: number

---@class predicate: userdata

---@class bufferstream: userdata

---@class constant_buffer: userdata

---@class render_target: userdata

---@class handle: userdata

---@class texture: userdata

---@class vector: table

---@class vector3
---@field x number
---@field y number
---@field z number

---@class vector4
---@field x number
---@field y number
---@field z number
---@field w number

---@class matrix4
---@field c0 vector4
---@field c1 vector4
---@field c2 vector4
---@field c3 vector4

---@class quat: vector4
---@class quaternion: vector4

---@module b2d

---
--- Get the Box2D body from a collision object
---@param url string | hash | url the url to the game object collision component
---@return b2Body the body if successful. Otherwise
function b2d.get_body(url) end

---
--- Get the Box2D world from the current collection
---@return b2World the world if successful. Otherwise
function b2d.get_world() end

---@module b2d.body
---@field B2_DYNAMIC_BODY number Dynamic body
---@field B2_KINEMATIC_BODY number Kinematic body
---@field B2_STATIC_BODY number Static (immovable) body


---
--- Apply an angular impulse.
---@param body b2Body body
---@param impulse number impulse the angular impulse in units of kg
function b2d.body.apply_angular_impulse(body, impulse) end

---
--- Apply a force at a world point. If the force is not
--- applied at the center of mass, it will generate a torque and
--- affect the angular velocity. This wakes up the body.
---@param body b2Body body
---@param force vector3 the world force vector, usually in Newtons (N).
---@param point vector3 the world position of the point of application.
function b2d.body.apply_force(body, force, point) end

---
--- Apply a force to the center of mass. This wakes up the body.
---@param body b2Body body
---@param force vector3 the world force vector, usually in Newtons (N).
function b2d.body.apply_force_to_center(body, force) end

---
--- Apply an impulse at a point. This immediately modifies the velocity.
--- It also modifies the angular velocity if the point of application
--- is not at the center of mass. This wakes up the body.
---@param body b2Body body
---@param impulse vector3 the world impulse vector, usually in N-seconds or kg-m/s.
---@param point vector3 the world position of the point of application.
function b2d.body.apply_linear_impulse(body, impulse, point) end

---
--- Apply a torque. This affects the angular velocity
--- without affecting the linear velocity of the center of mass.
--- This wakes up the body.
---@param body b2Body body
---@param torque number torque about the z-axis (out of the screen), usually in N-m.
function b2d.body.apply_torque(body, torque) end

---
--- Print the body representation to the log output
---@param body b2Body body
function b2d.body.dump(body) end

---
--- Get the angular damping of the body.
---@param body b2Body body
---@return number the damping
function b2d.body.get_angular_damping(body) end

---
--- Get the angular velocity.
---@overload fun(body: b2Body, omega: number): void Set the angular velocity.
---@param body b2Body body
---@return number the angular velocity in radians/second.
function b2d.body.get_angular_velocity(body) end

---
--- Get the gravity scale of the body.
---@param body b2Body body
---@return number the scale
function b2d.body.get_gravity_scale(body) end

---
--- Get the rotational inertia of the body about the local origin.
---@param body b2Body body
---@return number the rotational inertia, usually in kg-m^2.
function b2d.body.get_inertia(body) end

---
--- Get the linear damping of the body.
---@param body b2Body body
---@return number the damping
function b2d.body.get_linear_damping(body) end

---
--- Get the linear velocity of the center of mass.
---@param body b2Body body
---@return vector3 the linear velocity of the center of mass.
function b2d.body.get_linear_velocity(body) end

---
--- Get the world velocity of a local point.
---@overload fun(body: b2Body, world_point: vector3): vector3 Get the world linear velocity of a world point attached to this body.
---@param body b2Body body
---@param world_point vector3 a point in local coordinates.
---@return vector3 the world velocity of a point.
function b2d.body.get_linear_velocity_from_world_point(body, world_point) end

---
--- Get the local position of the center of mass.
---@param body b2Body body
---@return vector3 Get the local position of the center of mass.
function b2d.body.get_local_center(body) end

---
--- Gets a local point relative to the body's origin given a world point.
---@param body b2Body body
---@param world_point vector3 a point in world coordinates.
---@return vector3 the corresponding local point relative to the body's origin.
function b2d.body.get_local_point(body, world_point) end

---
--- Gets a local vector given a world vector.
---@param body b2Body body
---@param world_vector vector3 a vector in world coordinates.
---@return vector3 the corresponding local vector.
function b2d.body.get_local_vector(body, world_vector) end

---
--- Get the total mass of the body.
---@param body b2Body body
---@return number the mass, usually in kilograms (kg).
function b2d.body.get_mass(body) end

---
--- Get the next body in the world's body list.
---@param body b2Body body
---@return b2Body the next body
function b2d.body.get_next(body) end

---
--- Get the world body origin position.
---@param body b2Body body
---@return vector3 the world position of the body's origin.
function b2d.body.get_position(body) end

---
--- Get the type of this body.
---@param body b2Body body
---@return b2BodyType the body type
function b2d.body.get_type(body) end

---
--- Get the parent world of this body.
---@param body b2Body body
---@return b2World
function b2d.body.get_world(body) end

---
--- Get the world position of the center of mass.
---@overload fun(body: b2Body): number Get the angle in radians.
---@param body b2Body body
---@return vector3 Get the world position of the center of mass.
function b2d.body.get_world_center(body) end

---
--- Get the world coordinates of a point given the local coordinates.
---@param body b2Body body
---@param local_vector vector3 localPoint a point on the body measured relative the the body's origin.
---@return vector3 the same point expressed in world coordinates.
function b2d.body.get_world_point(body, local_vector) end

---
--- Get the world coordinates of a vector given the local coordinates.
---@param body b2Body body
---@param local_vector vector3 a vector fixed in the body.
---@return vector3 the same vector expressed in world coordinates.
function b2d.body.get_world_vector(body, local_vector) end

---
--- Get the active state of the body.
---@param body b2Body body
---@return boolean is the body active
function b2d.body.is_active(body) end

---
--- Get the sleeping state of this body.
---@param body b2Body body
---@return boolean true if the body is awake, false if it's sleeping.
function b2d.body.is_awake(body) end

---
--- Is this body in bullet mode
---@param body b2Body body
---@return boolean true if the body is in bullet mode
function b2d.body.is_bullet(body) end

---
--- Does this body have fixed rotation?
---@param body b2Body body
---@return boolean is the rotation fixed
function b2d.body.is_fixed_rotation(body) end

---
--- Is this body allowed to sleep
---@param body b2Body body
---@return boolean true if the body is allowed to sleep
function b2d.body.is_sleeping_allowed(body) end

---
--- This resets the mass properties to the sum of the mass properties of the fixtures.
--- This normally does not need to be called unless you called SetMassData to override
---@param body b2Body body
function b2d.body.reset_mass_data(body) end

---
--- Set the active state of the body. An inactive body is not
--- simulated and cannot be collided with or woken up.
--- If you pass a flag of true, all fixtures will be added to the
--- broad-phase.
--- If you pass a flag of false, all fixtures will be removed from
--- the broad-phase and all contacts will be destroyed.
--- Fixtures and joints are otherwise unaffected. You may continue
--- to create/destroy fixtures and joints on inactive bodies.
--- Fixtures on an inactive body are implicitly inactive and will
--- not participate in collisions, ray-casts, or queries.
--- Joints connected to an inactive body are implicitly inactive.
--- An inactive body is still owned by a b2World object and remains
--- in the body list.
---@param body b2Body body
---@param enable boolean true if the body should be active
function b2d.body.set_active(body, enable) end

---
--- Set the angular damping of the body.
---@param body b2Body body
---@param damping number the damping
function b2d.body.set_angular_damping(body, damping) end

---
--- Set the sleep state of the body. A sleeping body has very low CPU cost.
---@param body b2Body body
---@param enable boolean flag set to false to put body to sleep, true to wake it.
function b2d.body.set_awake(body, enable) end

---
--- Should this body be treated like a bullet for continuous collision detection?
---@param body b2Body body
---@param enable boolean if true, the body will be in bullet mode
function b2d.body.set_bullet(body, enable) end

---
--- Set this body to have fixed rotation. This causes the mass to be reset.
---@param body b2Body body
---@param enable boolean true if the rotation should be fixed
function b2d.body.set_fixed_rotation(body, enable) end

---
--- Set the gravity scale of the body.
---@param body b2Body body
---@param scale number the scale
function b2d.body.set_gravity_scale(body, scale) end

---
--- Set the linear damping of the body.
---@param body b2Body body
---@param damping number the damping
function b2d.body.set_linear_damping(body, damping) end

---
--- Set the linear velocity of the center of mass.
---@param body b2Body body
---@param velocity vector3 the new linear velocity of the center of mass.
function b2d.body.set_linear_velocity(body, velocity) end

---
--- You can disable sleeping on this body. If you disable sleeping, the body will be woken.
---@param body b2Body body
---@param enable boolean if false, the body will never sleep, and consume more CPU
function b2d.body.set_sleeping_allowed(body, enable) end

---
--- Set the position of the body's origin and rotation.
--- This breaks any contacts and wakes the other bodies.
--- Manipulating a body's transform may cause non-physical behavior.
---@param body b2Body body
---@param position vector3 the world position of the body's local origin.
---@param angle number the world position of the body's local origin.
function b2d.body.set_transform(body, position, angle) end

---
--- Set the type of this body. This may alter the mass and velocity.
---@param body b2Body body
---@param type b2BodyType the body type
function b2d.body.set_type(body, type) end

---@module buffer
---@field VALUE_TYPE_FLOAT32 number Float, single precision, 4 bytes
---@field VALUE_TYPE_INT16 number Signed integer, 2 bytes
---@field VALUE_TYPE_INT32 number Signed integer, 4 bytes
---@field VALUE_TYPE_INT64 number Signed integer, 8 bytes
---@field VALUE_TYPE_INT8 number Signed integer, 1 byte
---@field VALUE_TYPE_UINT16 number Unsigned integer, 2 bytes
---@field VALUE_TYPE_UINT32 number Unsigned integer, 4 bytes
---@field VALUE_TYPE_UINT64 number Unsigned integer, 8 bytes
---@field VALUE_TYPE_UINT8 number Unsigned integer, 1 byte


---
--- Copy all data streams from one buffer to another, element wise.
---  Each of the source streams must have a matching stream in the
--- destination buffer. The streams must match in both type and size.
--- The source and destination buffer can be the same.
---@param dst buffer the destination buffer
---@param dstoffset number the offset to start copying data to
---@param src buffer the source data buffer
---@param srcoffset number the offset to start copying data from
---@param count number the number of elements to copy
function buffer.copy_buffer(dst, dstoffset, src, srcoffset, count) end

---
--- Copy a specified amount of data from one stream to another.
---  The value type and size must match between source and destination streams.
--- The source and destination streams can be the same.
---@param dst bufferstream the destination stream
---@param dstoffset number the offset to start copying data to (measured in value type)
---@param src bufferstream the source data stream
---@param srcoffset number the offset to start copying data from (measured in value type)
---@param count number the number of values to copy (measured in value type)
function buffer.copy_stream(dst, dstoffset, src, srcoffset, count) end

---
--- Create a new data buffer containing a specified set of streams. A data buffer
--- can contain one or more streams with typed data. This is useful for managing
--- compound data, for instance a vertex buffer could contain separate streams for
--- vertex position, color, normal etc.
---@param element_count number The number of elements the buffer should hold
---@param declaration table A table where each entry (table) describes a stream
---@return buffer the new buffer
function buffer.create(element_count, declaration) end

---
--- Get a copy of all the bytes from a specified stream as a Lua string.
---@param buffer buffer the source buffer
---@param stream_name hash the name of the stream
---@return string the buffer data as a Lua string
function buffer.get_bytes(buffer, stream_name) end

---
--- Get a named metadata entry from a buffer along with its type.
---@param buf buffer the buffer to get the metadata from
---@param metadata_name hash | string name of the metadata entry
---@return table | nil table of metadata values or
function buffer.get_metadata(buf, metadata_name) end

---
--- Get a specified stream from a buffer.
---@param buffer buffer the buffer to get the stream from
---@param stream_name hash | string the stream name
---@return bufferstream the data stream
function buffer.get_stream(buffer, stream_name) end

---
--- Creates or updates a metadata array entry on a buffer.
---  The value type and count given when updating the entry should match those used when first creating it.
---@param buf buffer the buffer to set the metadata on
---@param metadata_name hash | string name of the metadata entry
---@param values table actual metadata, an array of numeric values
---@param value_type constant type of values when stored
function buffer.set_metadata(buf, metadata_name, values, value_type) end


---@module camera

---
--- makes camera active
---@param url string | hash | url url of camera component
function camera.acquire_focus(url) end

---
--- deactivate camera
---@param url string | hash | url url of camera component
function camera.release_focus(url) end

---@module collectionfactory
---@field STATUS_LOADED number loaded
---@field STATUS_LOADING number loading
---@field STATUS_UNLOADED number unloaded


---
--- The URL identifies the collectionfactory component that should do the spawning.
--- Spawning is instant, but spawned game objects get their first update calls the following frame. The supplied parameters for position, rotation and scale
--- will be applied to the whole collection when spawned.
--- Script properties in the created game objects can be overridden through
--- a properties-parameter table. The table should contain game object ids
--- (hash) as keys and property tables as values to be used when initiating each
--- spawned game object.
--- See go.property for more information on script properties.
--- The function returns a table that contains a key for each game object
--- id (hash), as addressed if the collection file was top level, and the
--- corresponding spawned instance id (hash) as value with a unique path
--- prefix added to each instance.
---  Calling collectionfactory.create create on a collection factory that is marked as dynamic without having loaded resources
--- using collectionfactory.load will synchronously load and create resources which may affect application performance.
---@param url string | hash | url the collection factory component to be used
---@param position vector3 [optional]position to assign to the newly spawned collection
---@param rotation quaternion [optional]rotation to assign to the newly spawned collection
---@param properties table [optional]table of script properties to propagate to any new game object instances
---@param scale number [optional]uniform scaling to apply to the newly spawned collection (must be greater than 0).
---@return table a table mapping the id:s from the collection to the new instance id:s
function collectionfactory.create(url, position, rotation, properties, scale) end

---
--- This returns status of the collection factory.
--- Calling this function when the factory is not marked as dynamic loading always returns COMP_COLLECTION_FACTORY_STATUS_LOADED.
---@param url string | hash | url [optional]the collection factory component to get status from
---@return constant status of the collection factory component
function collectionfactory.get_status(url) end

---
--- Resources loaded are referenced by the collection factory component until the existing (parent) collection is destroyed or collectionfactory.unload is called.
--- Calling this function when the factory is not marked as dynamic loading does nothing.
---@param url string | hash | url [optional]the collection factory component to load
---@param complete_function function(self, url, result) [optional]function to call when resources are loaded.
function collectionfactory.load(url, complete_function) end

---
--- Changes the prototype for the collection factory.
--- Setting the prototype to "nil" will revert back to the original prototype.
---@param url string | hash | url [optional]the collection factory component
---@param prototype string | nil [optional]the path to the new prototype, or
function collectionfactory.set_prototype(url, prototype) end

---
--- This decreases the reference count for each resource loaded with collectionfactory.load. If reference is zero, the resource is destroyed.
--- Calling this function when the factory is not marked as dynamic loading does nothing.
---@param url string | hash | url [optional]the collection factory component to unload
function collectionfactory.unload(url) end

---@module collectionproxy

---
--- return an indexed table of resources for a collection proxy. Each
--- entry is a hexadecimal string that represents the data of the specific
--- resource. This representation corresponds with the filename for each
--- individual resource that is exported when you bundle an application with
--- LiveUpdate functionality.
---@param collectionproxy url the collectionproxy to check for resources.
---@return table the resources
function collectionproxy.get_resources(collectionproxy) end

---
--- return an array of missing resources for a collection proxy. Each
--- entry is a hexadecimal string that represents the data of the specific
--- resource. This representation corresponds with the filename for each
--- individual resource that is exported when you bundle an application with
--- LiveUpdate functionality. It should be considered good practise to always
--- check whether or not there are any missing resources in a collection proxy
--- before attempting to load the collection proxy.
---@param collectionproxy url the collectionproxy to check for missing resources.
---@return table the missing resources
function collectionproxy.missing_resources(collectionproxy) end

---@module crash
---@field SYSFIELD_ANDROID_BUILD_FINGERPRINT number android build fingerprint
---@field SYSFIELD_DEVICE_LANGUAGE number system device language as reported by sys.get_sys_info
---@field SYSFIELD_DEVICE_MODEL number device model as reported by sys.get_sys_info
---@field SYSFIELD_ENGINE_HASH number engine version as hash
---@field SYSFIELD_ENGINE_VERSION number engine version as release number
---@field SYSFIELD_LANGUAGE number system language as reported by sys.get_sys_info
---@field SYSFIELD_MANUFACTURER number device manufacturer as reported by sys.get_sys_info
---@field SYSFIELD_MAX number The max number of sysfields.
---@field SYSFIELD_SYSTEM_NAME number system name as reported by sys.get_sys_info
---@field SYSFIELD_SYSTEM_VERSION number system version as reported by sys.get_sys_info
---@field SYSFIELD_TERRITORY number system territory as reported by sys.get_sys_info
---@field USERFIELD_MAX number The max number of user fields.
---@field USERFIELD_SIZE number The max size of a single user field.


---
--- A table is returned containing the addresses of the call stack.
---@param handle number crash dump handle
---@return table table containing the backtrace
function crash.get_backtrace(handle) end

---
--- The format of read text blob is platform specific
--- and not guaranteed
--- but can be useful for manual inspection.
---@param handle number crash dump handle
---@return string string with the platform specific data
function crash.get_extra_data(handle) end

---
--- The function returns a table containing entries with sub-tables that
--- have fields 'name' and 'address' set for all loaded modules.
---@param handle number crash dump handle
---@return table module table
function crash.get_modules(handle) end

---
--- read signal number from a crash report
---@param handle number crash dump handle
---@return number signal number
function crash.get_signum(handle) end

---
--- reads a system field from a loaded crash dump
---@param handle number crash dump handle
---@param index number system field enum. Must be less than
---@return string | nil value recorded in the crash dump, or
function crash.get_sys_field(handle, index) end

---
--- reads user field from a loaded crash dump
---@param handle number crash dump handle
---@param index number user data slot index
---@return string user data value recorded in the crash dump
function crash.get_user_field(handle, index) end

---
--- The crash dump will be removed from disk upon a successful
--- load, so loading is one-shot.
---@return number | nil handle to the loaded dump, or
function crash.load_previous() end

---
--- releases a previously loaded crash dump
---@param handle number handle to loaded crash dump
function crash.release(handle) end

---
--- Crashes occuring before the path is set will be stored to a default engine location.
---@param path string file path to use
function crash.set_file_path(path) end

---
--- Store a user value that will get written to a crash dump when
--- a crash occurs. This can be user id:s, breadcrumb data etc.
--- There are 32 slots indexed from 0. Each slot stores at most 255 characters.
---@param index number slot index. 0-indexed
---@param value string string value to store
function crash.set_user_field(index, value) end

---
--- Performs the same steps as if a crash had just occured but
--- allows the program to continue.
--- The generated dump can be read by crash.load_previous
function crash.write_dump() end


---@module factory
---@field STATUS_LOADED number loaded
---@field STATUS_LOADING number loading
---@field STATUS_UNLOADED number unloaded


---
--- The URL identifies which factory should create the game object.
--- If the game object is created inside of the frame (e.g. from an update callback), the game object will be created instantly, but none of its component will be updated in the same frame.
--- Properties defined in scripts in the created game object can be overridden through the properties-parameter below.
--- See go.property for more information on script properties.
---  Calling factory.create on a factory that is marked as dynamic without having loaded resources
--- using factory.load will synchronously load and create resources which may affect application performance.
---@param url string | hash | url the factory that should create a game object.
---@param position vector3 [optional]the position of the new game object, the position of the game object calling
---@param rotation quaternion [optional]the rotation of the new game object, the rotation of the game object calling
---@param properties table [optional]the properties defined in a script attached to the new game object.
---@param scale number | vector3 [optional]the scale of the new game object (must be greater than 0), the scale of the game object containing the factory is used by default, or if the value is
---@return hash the global id of the spawned game object
function factory.create(url, position, rotation, properties, scale) end

---
--- This returns status of the factory.
--- Calling this function when the factory is not marked as dynamic loading always returns
--- factory.STATUS_LOADED.
---@param url string | hash | url [optional]the factory component to get status from
---@return constant status of the factory component
function factory.get_status(url) end

---
--- Resources are referenced by the factory component until the existing (parent) collection is destroyed or factory.unload is called.
--- Calling this function when the factory is not marked as dynamic loading does nothing.
---@param url string | hash | url [optional]the factory component to load
---@param complete_function function(self, url, result) [optional]function to call when resources are loaded.
function factory.load(url, complete_function) end

---
--- Changes the prototype for the factory.
---@param url string | hash | url [optional]the factory component
---@param prototype string | nil [optional]the path to the new prototype, or
function factory.set_prototype(url, prototype) end

---
--- This decreases the reference count for each resource loaded with factory.load. If reference is zero, the resource is destroyed.
--- Calling this function when the factory is not marked as dynamic loading does nothing.
---@param url string | hash | url [optional]the factory component to unload
function factory.unload(url) end

---@module go
---@field EASING_INBACK number in-back
---@field EASING_INBOUNCE number in-bounce
---@field EASING_INCIRC number in-circlic
---@field EASING_INCUBIC number in-cubic
---@field EASING_INELASTIC number in-elastic
---@field EASING_INEXPO number in-exponential
---@field EASING_INOUTBACK number in-out-back
---@field EASING_INOUTBOUNCE number in-out-bounce
---@field EASING_INOUTCIRC number in-out-circlic
---@field EASING_INOUTCUBIC number in-out-cubic
---@field EASING_INOUTELASTIC number in-out-elastic
---@field EASING_INOUTEXPO number in-out-exponential
---@field EASING_INOUTQUAD number in-out-quadratic
---@field EASING_INOUTQUART number in-out-quartic
---@field EASING_INOUTQUINT number in-out-quintic
---@field EASING_INOUTSINE number in-out-sine
---@field EASING_INQUAD number in-quadratic
---@field EASING_INQUART number in-quartic
---@field EASING_INQUINT number in-quintic
---@field EASING_INSINE number in-sine
---@field EASING_LINEAR number linear interpolation
---@field EASING_OUTBACK number out-back
---@field EASING_OUTBOUNCE number out-bounce
---@field EASING_OUTCIRC number out-circlic
---@field EASING_OUTCUBIC number out-cubic
---@field EASING_OUTELASTIC number out-elastic
---@field EASING_OUTEXPO number out-exponential
---@field EASING_OUTINBACK number out-in-back
---@field EASING_OUTINBOUNCE number out-in-bounce
---@field EASING_OUTINCIRC number out-in-circlic
---@field EASING_OUTINCUBIC number out-in-cubic
---@field EASING_OUTINELASTIC number out-in-elastic
---@field EASING_OUTINEXPO number out-in-exponential
---@field EASING_OUTINQUAD number out-in-quadratic
---@field EASING_OUTINQUART number out-in-quartic
---@field EASING_OUTINQUINT number out-in-quintic
---@field EASING_OUTINSINE number out-in-sine
---@field EASING_OUTQUAD number out-quadratic
---@field EASING_OUTQUART number out-quartic
---@field EASING_OUTQUINT number out-quintic
---@field EASING_OUTSINE number out-sine
---@field PLAYBACK_LOOP_BACKWARD number loop backward
---@field PLAYBACK_LOOP_FORWARD number loop forward
---@field PLAYBACK_LOOP_PINGPONG number ping pong loop
---@field PLAYBACK_NONE number no playback
---@field PLAYBACK_ONCE_BACKWARD number once backward
---@field PLAYBACK_ONCE_FORWARD number once forward
---@field PLAYBACK_ONCE_PINGPONG number once ping pong


---
--- This is only supported for numerical properties. If the node property is already being
--- animated, that animation will be canceled and replaced by the new one.
--- If a complete_function (lua function) is specified, that function will be called when the animation has completed.
--- By starting a new animation in that function, several animations can be sequenced together. See the examples for more information.
---  If you call go.animate() from a game object's final() function,
--- any passed complete_function will be ignored and never called upon animation completion.
--- See the properties guide for which properties can be animated and the animation guide for how
--- them.
---@param url string | hash | url url of the game object or component having the property
---@param property string | hash id of the property to animate
---@param playback constant playback mode of the animation
---@param to number | vector3 | vector4 | quaternion target property value
---@param easing constant | vector easing to use during animation. Either specify a constant, see the
---@param duration number duration of the animation in seconds
---@param delay number [optional]delay before the animation starts in seconds
---@param complete_function function(self, url, property) [optional]optional function to call when the animation has completed
function go.animate(url, property, playback, to, easing, duration, delay, complete_function) end

---
--- By calling this function, all or specified stored property animations of the game object or component will be canceled.
--- See the properties guide for which properties can be animated and the animation guide for how to animate them.
---@param url string | hash | url url of the game object or component
---@param property string | hash [optional]optional id of the property to cancel
function go.cancel_animations(url, property) end

---
--- Delete one or more game objects identified by id. Deletion is asynchronous meaning that
--- the game object(s) are scheduled for deletion which will happen at the end of the current
--- frame. Note that game objects scheduled for deletion will be counted against
--- max_instances in "game.project" until they are actually removed.
---  Deleting a game object containing a particle FX component emitting particles will not immediately stop the particle FX from emitting particles. You need to manually stop the particle FX using particlefx.stop().
---  Deleting a game object containing a sound component that is playing will not immediately stop the sound from playing. You need to manually stop the sound using sound.stop().
---@param id string | hash | url | table [optional]optional id or table of id's of the instance(s) to delete, the instance of the calling script is deleted by default
---@param recursive boolean [optional]optional boolean, set to true to recursively delete child hiearchy in child to parent order
function go.delete(id, recursive) end

---
--- check if the specified game object exists
---@param url string | hash | url url of the game object to check
---@return boolean true if the game object exists
function go.exists(url) end

---
--- gets a named property of the specified game object or component
---@param url string | hash | url url of the game object or component having the property
---@param property string | hash id of the property to retrieve
---@param options table [optional]optional options table - index
---@return any the value of the specified property
function go.get(url, property, options) end

---
--- Returns or constructs an instance identifier. The instance id is a hash
--- of the absolute path to the instance.
---@param path string [optional]path of the instance for which to return the id
---@return hash instance id
function go.get_id(path) end

---
--- Get the parent for a game object instance.
---@param id string | hash | url [optional]optional id of the game object instance to get parent for, defaults to the instance containing the calling script
---@return hash | nil parent instance or
function go.get_parent(id) end

---
--- The position is relative the parent (if any). Use go.get_world_position to retrieve the global world position.
---@param id string | hash | url [optional]optional id of the game object instance to get the position for, by default the instance of the calling script
---@return vector3 instance position
function go.get_position(id) end

---
--- The rotation is relative to the parent (if any). Use go.get_world_rotation to retrieve the global world rotation.
---@param id string | hash | url [optional]optional id of the game object instance to get the rotation for, by default the instance of the calling script
---@return quaternion instance rotation
function go.get_rotation(id) end

---
--- The scale is relative the parent (if any). Use go.get_world_scale to retrieve the global world 3D scale factor.
---@param id string | hash | url [optional]optional id of the game object instance to get the scale for, by default the instance of the calling script
---@return vector3 instance scale factor
function go.get_scale(id) end

---
--- The uniform scale is relative the parent (if any). If the underlying scale vector is non-uniform the min element of the vector is returned as the uniform scale factor.
---@param id string | hash | url [optional]optional id of the game object instance to get the uniform scale for, by default the instance of the calling script
---@return number uniform instance scale factor
function go.get_scale_uniform(id) end

---
--- The function will return the world position calculated at the end of the previous frame.
--- Use go.get_position to retrieve the position relative to the parent.
---@param id string | hash | url [optional]optional id of the game object instance to get the world position for, by default the instance of the calling script
---@return vector3 instance world position
function go.get_world_position(id) end

---
--- The function will return the world rotation calculated at the end of the previous frame.
--- Use go.get_rotation to retrieve the rotation relative to the parent.
---@param id string | hash | url [optional]optional id of the game object instance to get the world rotation for, by default the instance of the calling script
---@return quaternion instance world rotation
function go.get_world_rotation(id) end

---
--- The function will return the world 3D scale factor calculated at the end of the previous frame.
--- Use go.get_scale to retrieve the 3D scale factor relative to the parent.
--- This vector is derived by decomposing the transformation matrix and should be used with care.
--- For most cases it should be fine to use go.get_world_scale_uniform instead.
---@param id string | hash | url [optional]optional id of the game object instance to get the world scale for, by default the instance of the calling script
---@return vector3 instance world 3D scale factor
function go.get_world_scale(id) end

---
--- The function will return the world scale factor calculated at the end of the previous frame.
--- Use go.get_scale_uniform to retrieve the scale factor relative to the parent.
---@param id string | hash | url [optional]optional id of the game object instance to get the world scale for, by default the instance of the calling script
---@return number instance world scale factor
function go.get_world_scale_uniform(id) end

---
--- The function will return the world transform matrix calculated at the end of the previous frame.
---@param id string | hash | url [optional]optional id of the game object instance to get the world transform for, by default the instance of the calling script
---@return matrix4 instance world transform
function go.get_world_transform(id) end

---
--- This function defines a property which can then be used in the script through the self-reference.
--- The properties defined this way are automatically exposed in the editor in game objects and collections which use the script.
--- Note that you can only use this function outside any callback-functions like init and update.
---@param name string the id of the property
---@param value number | hash | url | vector3 | vector4 | quaternion | resource | boolean default value of the property. In the case of a url, only the empty constructor msg.url() is allowed. In the case of a resource one of the resource constructors (eg resource.atlas(), resource.font() etc) is expected.
function go.property(name, value) end

---
--- sets a named property of the specified game object or component, or a material constant
---@param url string | hash | url url of the game object or component having the property
---@param property string | hash id of the property to set
---@param value any | table the value to set
---@param options table [optional]optional options table - index
function go.set(url, property, value, options) end

---
--- Sets the parent for a game object instance. This means that the instance will exist in the geometrical space of its parent,
--- like a basic transformation hierarchy or scene graph. If no parent is specified, the instance will be detached from any parent and exist in world
--- space.
--- This function will generate a set_parent message. It is not until the message has been processed that the change actually takes effect. This
--- typically happens later in the same frame or the beginning of the next frame. Refer to the manual to learn how messages are processed by the
--- engine.
---@param id string | hash | url [optional]optional id of the game object instance to set parent for, defaults to the instance containing the calling script
---@param parent_id string | hash | url [optional]optional id of the new parent game object, defaults to detaching game object from its parent
---@param keep_world_transform boolean [optional]optional boolean, set to true to maintain the world transform when changing spaces. Defaults to false.
function go.set_parent(id, parent_id, keep_world_transform) end

---
--- The position is relative to the parent (if any). The global world position cannot be manually set.
---@param position vector3 position to set
---@param id string | hash | url [optional]optional id of the game object instance to set the position for, by default the instance of the calling script
function go.set_position(position, id) end

---
--- The rotation is relative to the parent (if any). The global world rotation cannot be manually set.
---@param rotation quaternion rotation to set
---@param id string | hash | url [optional]optional id of the game object instance to get the rotation for, by default the instance of the calling script
function go.set_rotation(rotation, id) end

---
--- The scale factor is relative to the parent (if any). The global world scale factor cannot be manually set.
---  Physics are currently not affected when setting scale from this function.
---@param scale number | vector3 vector or uniform scale factor, must be greater than 0
---@param id string | hash | url [optional]optional id of the game object instance to get the scale for, by default the instance of the calling script
function go.set_scale(scale, id) end

---
--- The function uses world transformation calculated at the end of previous frame.
---@param position vector3 position which need to be converted
---@param url string | hash | url url of the game object which coordinate system convert to
---@return vector3 converted position
function go.world_to_local_position(position, url) end

---
--- The function uses world transformation calculated at the end of previous frame.
---@param transformation matrix4 transformation which need to be converted
---@param url string | hash | url url of the game object which coordinate system convert to
---@return matrix4 converted transformation
function go.world_to_local_transform(transformation, url) end

---@module gui
---@field ADJUST_FIT number Adjust mode is used when the screen resolution differs from the project settings. The fit mode ensures that the entire node is visible in the adjusted gui scene.
---@field ADJUST_STRETCH number Adjust mode is used when the screen resolution differs from the project settings. The stretch mode ensures that the node is displayed as is in the adjusted gui scene, which might scale it non-uniformally.
---@field ADJUST_ZOOM number Adjust mode is used when the screen resolution differs from the project settings. The zoom mode ensures that the node fills its entire area and might make the node exceed it.
---@field ANCHOR_BOTTOM number bottom y-anchor
---@field ANCHOR_LEFT number left x-anchor
---@field ANCHOR_NONE number no anchor
---@field ANCHOR_RIGHT number right x-anchor
---@field ANCHOR_TOP number top y-anchor
---@field BLEND_ADD number additive blending
---@field BLEND_ADD_ALPHA number additive alpha blending
---@field BLEND_ALPHA number alpha blending
---@field BLEND_MULT number multiply blending
---@field BLEND_SCREEN number screen blending
---@field CLIPPING_MODE_NONE number clipping mode none
---@field CLIPPING_MODE_STENCIL number clipping mode stencil
---@field EASING_INBACK number in-back
---@field EASING_INBOUNCE number in-bounce
---@field EASING_INCIRC number in-circlic
---@field EASING_INCUBIC number in-cubic
---@field EASING_INELASTIC number in-elastic
---@field EASING_INEXPO number in-exponential
---@field EASING_INOUTBACK number in-out-back
---@field EASING_INOUTBOUNCE number in-out-bounce
---@field EASING_INOUTCIRC number in-out-circlic
---@field EASING_INOUTCUBIC number in-out-cubic
---@field EASING_INOUTELASTIC number in-out-elastic
---@field EASING_INOUTEXPO number in-out-exponential
---@field EASING_INOUTQUAD number in-out-quadratic
---@field EASING_INOUTQUART number in-out-quartic
---@field EASING_INOUTQUINT number in-out-quintic
---@field EASING_INOUTSINE number in-out-sine
---@field EASING_INQUAD number in-quadratic
---@field EASING_INQUART number in-quartic
---@field EASING_INQUINT number in-quintic
---@field EASING_INSINE number in-sine
---@field EASING_LINEAR number linear interpolation
---@field EASING_OUTBACK number out-back
---@field EASING_OUTBOUNCE number out-bounce
---@field EASING_OUTCIRC number out-circlic
---@field EASING_OUTCUBIC number out-cubic
---@field EASING_OUTELASTIC number out-elastic
---@field EASING_OUTEXPO number out-exponential
---@field EASING_OUTINBACK number out-in-back
---@field EASING_OUTINBOUNCE number out-in-bounce
---@field EASING_OUTINCIRC number out-in-circlic
---@field EASING_OUTINCUBIC number out-in-cubic
---@field EASING_OUTINELASTIC number out-in-elastic
---@field EASING_OUTINEXPO number out-in-exponential
---@field EASING_OUTINQUAD number out-in-quadratic
---@field EASING_OUTINQUART number out-in-quartic
---@field EASING_OUTINQUINT number out-in-quintic
---@field EASING_OUTINSINE number out-in-sine
---@field EASING_OUTQUAD number out-quadratic
---@field EASING_OUTQUART number out-quartic
---@field EASING_OUTQUINT number out-quintic
---@field EASING_OUTSINE number out-sine
---@field KEYBOARD_TYPE_DEFAULT number default keyboard
---@field KEYBOARD_TYPE_EMAIL number email keyboard
---@field KEYBOARD_TYPE_NUMBER_PAD number number input keyboard
---@field KEYBOARD_TYPE_PASSWORD number password keyboard
---@field PIEBOUNDS_ELLIPSE number elliptical pie node bounds
---@field PIEBOUNDS_RECTANGLE number rectangular pie node bounds
---@field PIVOT_CENTER number center pivot
---@field PIVOT_E number east pivot
---@field PIVOT_N number north pivot
---@field PIVOT_NE number north-east pivot
---@field PIVOT_NW number north-west pivot
---@field PIVOT_S number south pivot
---@field PIVOT_SE number south-east pivot
---@field PIVOT_SW number south-west pivot
---@field PIVOT_W number west pivot
---@field PLAYBACK_LOOP_BACKWARD number loop backward
---@field PLAYBACK_LOOP_FORWARD number loop forward
---@field PLAYBACK_LOOP_PINGPONG number ping pong loop
---@field PLAYBACK_ONCE_BACKWARD number once backward
---@field PLAYBACK_ONCE_FORWARD number once forward
---@field PLAYBACK_ONCE_PINGPONG number once forward and then backward
---@field PROP_COLOR number color property
---@field PROP_EULER number euler property
---@field PROP_FILL_ANGLE number fill_angle property
---@field PROP_INNER_RADIUS number inner_radius property
---@field PROP_OUTLINE number outline color property
---@field PROP_POSITION number position property
---@field PROP_ROTATION number rotation property
---@field PROP_SCALE number scale property
---@field PROP_SHADOW number shadow color property
---@field PROP_SIZE number size property
---@field PROP_SLICE9 number slice9 property
---@field RESULT_DATA_ERROR number The provided data is not in the expected format or is in some other way incorrect, for instance the image data provided to gui.new_texture().
---@field RESULT_OUT_OF_RESOURCES number The system is out of resources, for instance when trying to create a new texture using gui.new_texture().
---@field RESULT_TEXTURE_ALREADY_EXISTS number The texture id already exists when trying to use gui.new_texture().
---@field SIZE_MODE_AUTO number The size of the node is determined by the currently assigned texture.
---@field SIZE_MODE_MANUAL number The size of the node is determined by the size set in the editor, the constructor or by gui.set_size()


---
--- This starts an animation of a node property according to the specified parameters.
--- If the node property is already being animated, that animation will be canceled and
--- replaced by the new one. Note however that several different node properties
--- can be animated simultaneously. Use gui.cancel_animation to stop the animation
--- before it has completed.
--- Composite properties of type vector3, vector4 or quaternion
--- also expose their sub-components (x, y, z and w).
--- You can address the components individually by suffixing the name with a dot '.'
--- and the name of the component.
--- For instance, "position.x" (the position x coordinate) or "color.w"
--- (the color alpha value).
--- If a complete_function (Lua function) is specified, that function will be called
--- when the animation has completed.
--- By starting a new animation in that function, several animations can be sequenced
--- together. See the examples below for more information.
---@param node node node to animate
---@param property string | constant property to animate
---@param to number | vector3 | vector4 | quaternion target property value
---@param easing constant | vector easing to use during animation.      Either specify one of the
---@param duration number duration of the animation in seconds.
---@param delay number [optional]delay before the animation starts in seconds.
---@param complete_function function(self, node) [optional]function to call when the      animation has completed
---@param playback constant [optional]playback mode
function gui.animate(node, property, to, easing, duration, delay, complete_function, playback) end

---
--- If an animation of the specified node is currently running (started by gui.animate), it will immediately be canceled.
---@param node node node that should have its animation canceled
---@param property string | constant property for which the animation should be canceled
function gui.cancel_animation(node, property) end

---
--- Cancels any running flipbook animation on the specified node.
---@param node node node cancel flipbook animation for
function gui.cancel_flipbook(node) end

---
--- Make a clone instance of a node. The cloned node will be identical to the
--- original node, except the id which is generated as the string "node" plus
--- a sequential unsigned integer value.
--- This function does not clone the supplied node's children nodes.
--- Use gui.clone_tree for that purpose.
---@param node node node to clone
---@return node the cloned node
function gui.clone(node) end

---
--- Make a clone instance of a node and all its children.
--- Use gui.clone to clone a node excluding its children.
---@param node node root node to clone
---@return table a table mapping node ids to the corresponding cloned nodes
function gui.clone_tree(node) end

---
--- Deletes the specified node. Any child nodes of the specified node will be
--- recursively deleted.
---@param node node node to delete
function gui.delete_node(node) end

---
--- Delete a dynamically created texture.
---@param texture string | hash texture id
function gui.delete_texture(texture) end

---
--- Instead of using specific getters such as gui.get_position or gui.get_scale,
--- you can use gui.get instead and supply the property as a string or a hash.
--- While this function is similar to go.get, there are a few more restrictions
--- when operating in the gui namespace. Most notably, only these propertie identifiers are supported:
---@param node node node to get the property for
---@param property string | hash | constant the property to retrieve
function gui.get(node, property) end

---
--- Returns the adjust mode of a node.
--- The adjust mode defines how the node will adjust itself to screen
--- resolutions that differs from the one in the project settings.
---@param node node node from which to get the adjust mode (node)
---@return constant the current adjust mode
function gui.get_adjust_mode(node) end

---
--- gets the node alpha
---@param node node node from which to get alpha
function gui.get_alpha(node) end

---
--- Returns the blend mode of a node.
--- Blend mode defines how the node will be blended with the background.
---@param node node node from which to get the blend mode
---@return constant blend mode
function gui.get_blend_mode(node) end

---
--- If node is set as an inverted clipping node, it will clip anything inside as opposed to outside.
---@param node node node from which to get the clipping inverted state
---@return boolean true or false
function gui.get_clipping_inverted(node) end

---
--- Clipping mode defines how the node will clip it's children nodes
---@param node node node from which to get the clipping mode
---@return constant clipping mode
function gui.get_clipping_mode(node) end

---
--- If node is set as visible clipping node, it will be shown as well as clipping. Otherwise, it will only clip but not show visually.
---@param node node node from which to get the clipping visibility state
---@return boolean true or false
function gui.get_clipping_visible(node) end

---
--- Returns the color of the supplied node. The components
--- of the returned vector4 contains the color channel values:
---@param node node node to get the color from
---@return vector4 node color
function gui.get_color(node) end

---
--- Returns the rotation of the supplied node.
--- The rotation is expressed in degree Euler angles.
---@param node node node to get the rotation from
---@return vector3 node rotation
function gui.get_euler(node) end

---
--- Returns the sector angle of a pie node.
---@param node node node from which to get the fill angle
---@return number sector angle
function gui.get_fill_angle(node) end

---
--- Get node flipbook animation.
---@param node node node to get flipbook animation from
---@return hash animation id
function gui.get_flipbook(node) end

---
--- This is only useful nodes with flipbook animations. Gets the normalized cursor of the flipbook animation on a node.
---@param node node node to get the cursor for (node)
---@return number cursor value
function gui.get_flipbook_cursor(node) end

---
--- This is only useful nodes with flipbook animations. Gets the playback rate of the flipbook animation on a node.
---@param node node node to set the cursor for
---@return number playback rate
function gui.get_flipbook_playback_rate(node) end

---
--- This is only useful for text nodes. The font must be mapped to the gui scene in the gui editor.
---@param node node node from which to get the font
---@return hash font id
function gui.get_font(node) end

---
--- This is only useful for text nodes. The font must be mapped to the gui scene in the gui editor.
---@param font_name hash | string font of which to get the path hash
---@return hash path hash to resource
function gui.get_font_resource(font_name) end

---
--- Returns the scene height.
---@return number scene height
function gui.get_height() end

---
--- Retrieves the id of the specified node.
---@param node node the node to retrieve the id from
---@return hash the id of the node
function gui.get_id(node) end

---
--- Retrieve the index of the specified node among its siblings.
--- The index defines the order in which a node appear in a GUI scene.
--- Higher index means the node is drawn on top of lower indexed nodes.
---@param node node the node to retrieve the id from
---@return number the index of the node
function gui.get_index(node) end

---
--- gets the node inherit alpha state
---@param node node node from which to get the inherit alpha state
function gui.get_inherit_alpha(node) end

---
--- Returns the inner radius of a pie node.
--- The radius is defined along the x-axis.
---@param node node node from where to get the inner radius
---@return number inner radius
function gui.get_inner_radius(node) end

---
--- The layer must be mapped to the gui scene in the gui editor.
---@param node node node from which to get the layer
---@return hash layer id
function gui.get_layer(node) end

---
--- gets the scene current layout
---@return hash layout id
function gui.get_layout() end

---
--- Returns the leading value for a text node.
---@param node node node from where to get the leading
---@return number leading scaling value (default=1)
function gui.get_leading(node) end

---
--- Returns whether a text node is in line-break mode or not.
--- This is only useful for text nodes.
---@param node node node from which to get the line-break for
---@return boolean
function gui.get_line_break(node) end

---
--- Returns the material of a node.
--- The material must be mapped to the gui scene in the gui editor.
---@param node node node to get the material for
function gui.get_material(node) end

---
--- Retrieves the node with the specified id.
---@param id string | hash id of the node to retrieve
---@return node a new node instance
function gui.get_node(id) end

---
--- Returns the outer bounds mode for a pie node.
---@param node node node from where to get the outer bounds mode
---@return constant the outer bounds mode of the pie node:
function gui.get_outer_bounds(node) end

---
--- Returns the outline color of the supplied node.
--- See gui.get_color for info how vectors encode color values.
---@param node node node to get the outline color from
---@return vector4 outline color
function gui.get_outline(node) end

---
--- Returns the parent node of the specified node.
--- If the supplied node does not have a parent, nil is returned.
---@param node node the node from which to retrieve its parent
---@return node | nil parent instance or
function gui.get_parent(node) end

---
--- Get the paricle fx for a gui node
---@param node node node to get particle fx for
---@return hash particle fx id
function gui.get_particlefx(node) end

---
--- Returns the number of generated vertices around the perimeter
--- of a pie node.
---@param node node pie node
---@return number vertex count
function gui.get_perimeter_vertices(node) end

---
--- The pivot specifies how the node is drawn and rotated from its position.
---@param node node node to get pivot from
---@return constant pivot constant
function gui.get_pivot(node) end

---
--- Returns the position of the supplied node.
---@param node node node to get the position from
---@return vector3 node position
function gui.get_position(node) end

---
--- Returns the rotation of the supplied node.
--- The rotation is expressed as a quaternion
---@param node node node to get the rotation from
---@return quat node rotation
function gui.get_rotation(node) end

---
--- Returns the scale of the supplied node.
---@param node node node to get the scale from
---@return vector3 node scale
function gui.get_scale(node) end

---
--- Returns the screen position of the supplied node. This function returns the
--- calculated transformed position of the node, taking into account any parent node
--- transforms.
---@param node node node to get the screen position from
---@return vector3 node screen position
function gui.get_screen_position(node) end

---
--- Returns the shadow color of the supplied node.
--- See gui.get_color for info how vectors encode color values.
---@param node node node to get the shadow color from
---@return vector4 node shadow color
function gui.get_shadow(node) end

---
--- Returns the size of the supplied node.
---@param node node node to get the size from
---@return vector3 node size
function gui.get_size(node) end

---
--- Returns the size of a node.
--- The size mode defines how the node will adjust itself in size. Automatic
--- size mode alters the node size based on the node's content. Automatic size
--- mode works for Box nodes and Pie nodes which will both adjust their size
--- to match the assigned image. Particle fx and Text nodes will ignore
--- any size mode setting.
---@param node node node from which to get the size mode (node)
---@return constant the current size mode
function gui.get_size_mode(node) end

---
--- Returns the slice9 configuration values for the node.
---@param node node node to manipulate
---@return vector4 configuration values
function gui.get_slice9(node) end

---
--- Returns the text value of a text node. This is only useful for text nodes.
---@param node node node from which to get the text
---@return string text value
function gui.get_text(node) end

---
--- Returns the texture of a node.
--- This is currently only useful for box or pie nodes.
--- The texture must be mapped to the gui scene in the gui editor.
---@param node node node to get texture from
---@return hash texture id
function gui.get_texture(node) end

---
--- Returns the tracking value of a text node.
---@param node node node from where to get the tracking
---@return number tracking scaling number (default=0)
function gui.get_tracking(node) end

---
--- Get a node and all its children as a Lua table.
---@param node node root node to get node tree from
---@return table a table mapping node ids to the corresponding nodes
function gui.get_tree(node) end

---
--- Returns true if a node is visible and false if it's not.
--- Invisible nodes are not rendered.
---@param node node node to query
---@return boolean whether the node is visible or not
function gui.get_visible(node) end

---
--- Returns the scene width.
---@return number scene width
function gui.get_width() end

---
--- The x-anchor specifies how the node is moved when the game is run in a different resolution.
---@param node node node to get x-anchor from
---@return constant anchor constant
function gui.get_xanchor(node) end

---
--- The y-anchor specifies how the node is moved when the game is run in a different resolution.
---@param node node node to get y-anchor from
---@return constant anchor constant
function gui.get_yanchor(node) end

---
--- Hides the on-display touch keyboard on the device.
function gui.hide_keyboard() end

---
--- Returns true if a node is enabled and false if it's not.
--- Disabled nodes are not rendered and animations acting on them are not evaluated.
---@param node node node to query
---@param recursive boolean check hierarchy recursively
---@return boolean whether the node is enabled or not
function gui.is_enabled(node, recursive) end

---
--- Alters the ordering of the two supplied nodes by moving the first node
--- above the second.
--- If the second argument is nil the first node is moved to the top.
---@param node node to move
---@param reference node | nil reference node above which the first node should be moved
function gui.move_above(node, reference) end

---
--- Alters the ordering of the two supplied nodes by moving the first node
--- below the second.
--- If the second argument is nil the first node is moved to the bottom.
---@param node node to move
---@param reference node | nil reference node below which the first node should be moved
function gui.move_below(node, reference) end

---
--- Dynamically create a new box node.
---@param pos vector3 | vector4 node position
---@param size vector3 node size
---@return node new box node
function gui.new_box_node(pos, size) end

---
--- Dynamically create a particle fx node.
---@param pos vector3 | vector4 node position
---@param particlefx hash | string particle fx resource name
---@return node new particle fx node
function gui.new_particlefx_node(pos, particlefx) end

---
--- Dynamically create a new pie node.
---@param pos vector3 | vector4 node position
---@param size vector3 node size
---@return node new pie node
function gui.new_pie_node(pos, size) end

---
--- Dynamically create a new text node.
---@param pos vector3 | vector4 node position
---@param text string node text
---@return node new text node
function gui.new_text_node(pos, text) end

---
--- Dynamically create a new texture.
---@param texture_id string | hash texture id
---@param width number texture width
---@param height number texture height
---@param type string | constant texture type
---@param buffer string texture data
---@param flip boolean flip texture vertically
---@return boolean texture creation was successful
function gui.new_texture(texture_id, width, height, type, buffer, flip) end

---
--- Tests whether a coordinate is within the bounding box of a
--- node.
---@param node node node to be tested for picking
---@param x number x-coordinate (see
---@param y number y-coordinate (see
---@return boolean pick result
function gui.pick_node(node, x, y) end

---
--- Play flipbook animation on a box or pie node.
--- The current node texture must contain the animation.
--- Use this function to set one-frame still images on the node.
---@param node node node to set animation for
---@param animation string | hash animation id
---@param complete_function function(self, node) [optional]optional function to call when the animation has completed
---@param play_properties table [optional]optional table with properties
function gui.play_flipbook(node, animation, complete_function, play_properties) end

---
--- Plays the paricle fx for a gui node
---@param node node node to play particle fx for
---@param emitter_state_function function(self, node, emitter, state) [optional]optional callback function that will be called when an emitter attached to this particlefx changes state.
function gui.play_particlefx(node, emitter_state_function) end

---
--- Resets the input context of keyboard. This will clear marked text.
function gui.reset_keyboard() end

---
--- Resets the node material to the material assigned in the gui scene.
---@param node node node to reset the material for
function gui.reset_material(node) end

---
--- Resets all nodes in the current GUI scene to their initial state.
--- The reset only applies to static node loaded from the scene.
--- Nodes that are created dynamically from script are not affected.
function gui.reset_nodes() end

---
--- Convert the screen position to the local position of supplied node
---@param node node node used for getting local transformation matrix
---@param screen_position vector3 screen position
---@return vector3 local position
function gui.screen_to_local(node, screen_position) end

---
--- Instead of using specific setteres such as gui.set_position or gui.set_scale,
--- you can use gui.set instead and supply the property as a string or a hash.
--- While this function is similar to go.get and go.set, there are a few more restrictions
--- when operating in the gui namespace. Most notably, only these propertie identifiers are supported:
---@param node node node to set the property for
---@param property string | hash | constant the property to set
---@param value number | vector4 | vector3 | quat the property to set
function gui.set(node, property, value) end

---
--- Sets the adjust mode on a node.
--- The adjust mode defines how the node will adjust itself to screen
--- resolutions that differs from the one in the project settings.
---@param node node node to set adjust mode for
---@param adjust_mode constant adjust mode to set
function gui.set_adjust_mode(node, adjust_mode) end

---
--- sets the node alpha
---@param node node node for which to set alpha
---@param alpha number 0..1 alpha color
function gui.set_alpha(node, alpha) end

---
--- Set the blend mode of a node.
--- Blend mode defines how the node will be blended with the background.
---@param node node node to set blend mode for
---@param blend_mode constant blend mode to set
function gui.set_blend_mode(node, blend_mode) end

---
--- If node is set as an inverted clipping node, it will clip anything inside as opposed to outside.
---@param node node node to set clipping inverted state for
---@param inverted boolean true or false
function gui.set_clipping_inverted(node, inverted) end

---
--- Clipping mode defines how the node will clip it's children nodes
---@param node node node to set clipping mode for
---@param clipping_mode constant clipping mode to set
function gui.set_clipping_mode(node, clipping_mode) end

---
--- If node is set as an visible clipping node, it will be shown as well as clipping. Otherwise, it will only clip but not show visually.
---@param node node node to set clipping visibility for
---@param visible boolean true or false
function gui.set_clipping_visible(node, visible) end

---
--- Sets the color of the supplied node. The components
--- of the supplied vector3 or vector4 should contain the color channel values:
---@param node node node to set the color for
---@param color vector3 | vector4 new color
function gui.set_color(node, color) end

---
--- Sets a node to the disabled or enabled state.
--- Disabled nodes are not rendered and animations acting on them are not evaluated.
---@param node node node to be enabled/disabled
---@param enabled boolean whether the node should be enabled or not
function gui.set_enabled(node, enabled) end

---
--- Sets the rotation of the supplied node.
--- The rotation is expressed in degree Euler angles.
---@param node node node to set the rotation for
---@param rotation vector3 | vector4 new rotation
function gui.set_euler(node, rotation) end

---
--- Set the sector angle of a pie node.
---@param node node node to set the fill angle for
---@param angle number sector angle
function gui.set_fill_angle(node, angle) end

---
--- This is only useful nodes with flipbook animations. The cursor is normalized.
---@param node node node to set the cursor for
---@param cursor number cursor value
function gui.set_flipbook_cursor(node, cursor) end

---
--- This is only useful nodes with flipbook animations. Sets the playback rate of the flipbook animation on a node. Must be positive.
---@param node node node to set the cursor for
---@param playback_rate number playback rate
function gui.set_flipbook_playback_rate(node, playback_rate) end

---
--- This is only useful for text nodes.
--- The font must be mapped to the gui scene in the gui editor.
---@param node node node for which to set the font
---@param font string | hash font id
function gui.set_font(node, font) end

---
--- Set the id of the specicied node to a new value.
--- Nodes created with the gui.new_*_node() functions get
--- an empty id. This function allows you to give dynamically
--- created nodes an id.
---  No checking is done on the uniqueness of supplied ids.
--- It is up to you to make sure you use unique ids.
---@param node node node to set the id for
---@param id string | hash id to set
function gui.set_id(node, id) end

---
--- sets the node inherit alpha state
---@param node node node from which to set the inherit alpha state
---@param inherit_alpha boolean true or false
function gui.set_inherit_alpha(node, inherit_alpha) end

---
--- Sets the inner radius of a pie node.
--- The radius is defined along the x-axis.
---@param node node node to set the inner radius for
---@param radius number inner radius
function gui.set_inner_radius(node, radius) end

---
--- The layer must be mapped to the gui scene in the gui editor.
---@param node node node for which to set the layer
---@param layer string | hash layer id
function gui.set_layer(node, layer) end

---
--- Sets the leading value for a text node. This value is used to
--- scale the line spacing of text.
---@param node node node for which to set the leading
---@param leading number a scaling value for the line spacing (default=1)
function gui.set_leading(node, leading) end

---
--- Sets the line-break mode on a text node.
--- This is only useful for text nodes.
---@param node node node to set line-break for
---@param line_break boolean true or false
function gui.set_line_break(node, line_break) end

---
--- Set the material on a node. The material must be mapped to the gui scene in the gui editor,
--- and assigning a material is supported for all node types. To set the default material that
--- is assigned to the gui scene node, use gui.reset_material(node_id) instead.
---@param node node node to set material for
---@param material string | hash material id
function gui.set_material(node, material) end

---
--- Sets the outer bounds mode for a pie node.
---@param node node node for which to set the outer bounds mode
---@param bounds_mode constant the outer bounds mode of the pie node:
function gui.set_outer_bounds(node, bounds_mode) end

---
--- Sets the outline color of the supplied node.
--- See gui.set_color for info how vectors encode color values.
---@param node node node to set the outline color for
---@param color vector3 | vector4 new outline color
function gui.set_outline(node, color) end

---
--- Sets the parent node of the specified node.
---@param node node node for which to set its parent
---@param parent node parent node to set
---@param keep_scene_transform boolean optional flag to make the scene position being perserved
function gui.set_parent(node, parent, keep_scene_transform) end

---
--- Set the paricle fx for a gui node
---@param node node node to set particle fx for
---@param particlefx hash | string particle fx id
function gui.set_particlefx(node, particlefx) end

---
--- Sets the number of generated vertices around the perimeter of a pie node.
---@param node node pie node
---@param vertices number vertex count
function gui.set_perimeter_vertices(node, vertices) end

---
--- The pivot specifies how the node is drawn and rotated from its position.
---@param node node node to set pivot for
---@param pivot constant pivot constant
function gui.set_pivot(node, pivot) end

---
--- Sets the position of the supplied node.
---@param node node node to set the position for
---@param position vector3 | vector4 new position
function gui.set_position(node, position) end

---
--- Set the order number for the current GUI scene.
--- The number dictates the sorting of the "gui" render predicate,
--- in other words in which order the scene will be rendered in relation
--- to other currently rendered GUI scenes.
--- The number must be in the range 0 to 15.
---@param order number rendering order (0-15)
function gui.set_render_order(order) end

---
--- Sets the rotation of the supplied node.
--- The rotation is expressed as a quaternion
---@param node node node to set the rotation for
---@param rotation quat | vector4 new rotation
function gui.set_rotation(node, rotation) end

---
--- Sets the scaling of the supplied node.
---@param node node node to set the scale for
---@param scale vector3 | vector4 new scale
function gui.set_scale(node, scale) end

---
--- Set the screen position to the supplied node
---@param node node node to set the screen position to
---@param screen_position vector3 screen position
function gui.set_screen_position(node, screen_position) end

---
--- Sets the shadow color of the supplied node.
--- See gui.set_color for info how vectors encode color values.
---@param node node node to set the shadow color for
---@param color vector3 | vector4 new shadow color
function gui.set_shadow(node, color) end

---
--- Sets the size of the supplied node.
---  You can only set size on nodes with size mode set to SIZE_MODE_MANUAL
---@param node node node to set the size for
---@param size vector3 | vector4 new size
function gui.set_size(node, size) end

---
--- Sets the size mode of a node.
--- The size mode defines how the node will adjust itself in size. Automatic
--- size mode alters the node size based on the node's content. Automatic size
--- mode works for Box nodes and Pie nodes which will both adjust their size
--- to match the assigned image. Particle fx and Text nodes will ignore
--- any size mode setting.
---@param node node node to set size mode for
---@param size_mode constant size mode to set
function gui.set_size_mode(node, size_mode) end

---
--- Set the slice9 configuration values for the node.
---@param node node node to manipulate
---@param values vector4 new values
function gui.set_slice9(node, values) end

---
--- Set the text value of a text node. This is only useful for text nodes.
---@param node node node to set text for
---@param text string text to set
function gui.set_text(node, text) end

---
--- Set the texture on a box or pie node. The texture must be mapped to
--- the gui scene in the gui editor. The function points out which texture
--- the node should render from. If the texture is an atlas, further
--- information is needed to select which image/animation in the atlas
--- to render. In such cases, use gui.play_flipbook() in
--- addition to this function.
---@param node node node to set texture for
---@param texture string | hash texture id
function gui.set_texture(node, texture) end

---
--- Set the texture buffer data for a dynamically created texture.
---@param texture string | hash texture id
---@param width number texture width
---@param height number texture height
---@param type string | constant texture type
---@param buffer string texture data
---@param flip boolean flip texture vertically
---@return boolean setting the data was successful
function gui.set_texture_data(texture, width, height, type, buffer, flip) end

---
--- Sets the tracking value of a text node. This value is used to
--- adjust the vertical spacing of characters in the text.
---@param node node node for which to set the tracking
---@param tracking number a scaling number for the letter spacing (default=0)
function gui.set_tracking(node, tracking) end

---
--- Set if a node should be visible or not. Only visible nodes are rendered.
---@param node node node to be visible or not
---@param visible boolean whether the node should be visible or not
function gui.set_visible(node, visible) end

---
--- The x-anchor specifies how the node is moved when the game is run in a different resolution.
---@param node node node to set x-anchor for
---@param anchor constant anchor constant
function gui.set_xanchor(node, anchor) end

---
--- The y-anchor specifies how the node is moved when the game is run in a different resolution.
---@param node node node to set y-anchor for
---@param anchor constant anchor constant
function gui.set_yanchor(node, anchor) end

---
--- Shows the on-display touch keyboard.
--- The specified type of keyboard is displayed if it is available on
--- the device.
--- This function is only available on iOS and Android.  .
---@param type constant keyboard type
---@param autoclose boolean if the keyboard should automatically close when clicking outside
function gui.show_keyboard(type, autoclose) end

---
--- Stops the particle fx for a gui node
---@param node node node to stop particle fx for
---@param options table options when stopping the particle fx. Supported options:
function gui.stop_particlefx(node, options) end

---@module html5

---
--- Executes the supplied string as JavaScript inside the browser.
--- A call to this function is blocking, the result is returned as-is, as a string.
--- (Internally this will execute the string using the eval() JavaScript function.)
---@param code string Javascript code to run
---@return string result as string
function html5.run(code) end

---
--- Set a JavaScript interaction listener callaback from lua that will be
--- invoked when a user interacts with the web page by clicking, touching or typing.
--- The callback can then call DOM restricted actions like requesting a pointer lock,
--- or start playing sounds the first time the callback is invoked.
---@param callback function(self) | nil The interaction callback. Pass an empty function or
function html5.set_interaction_listener(callback) end

---@module http

---
--- Perform a HTTP/HTTPS request.
---  If no timeout value is passed, the configuration value "network.http_timeout" is used. If that is not set, the timeout value is 0 (which blocks indefinitely).
---@param url string target url
---@param method string HTTP/HTTPS method, e.g. "GET", "PUT", "POST" etc.
---@param callback function(self, id, response) response callback function
---@param headers table [optional]optional table with custom headers
---@param post_data string [optional]optional data to send
---@param options table [optional]optional table with request parameters. Supported entries:
function http.request(url, method, callback, headers, post_data, options) end

---@module image
---@field TYPE_LUMINANCE number luminance image type
---@field TYPE_LUMINANCE_ALPHA number luminance image type
---@field TYPE_RGB number RGB image type
---@field TYPE_RGBA number RGBA image type


---
--- Load image (PNG or JPEG) from buffer.
---@param buffer string image data buffer
---@param options table [optional]An optional table containing parameters for loading the image. Supported entries:
---@return table | nil object or
function image.load(buffer, options) end

---
--- Load image (PNG or JPEG) from a string buffer.
---@param buffer string image data buffer
---@param options table [optional]An optional table containing parameters for loading the image. Supported entries:
---@return table | nil object or
function image.load_buffer(buffer, options) end

---@module json
---@field null number Represents the null primitive from a json file


---
--- Decode a string of JSON data into a Lua table.
--- A Lua error is raised for syntax errors.
---@param json string json data
---@param options table table with decode options
---@return table decoded json
function json.decode(json, options) end

---
--- Encode a lua table to a JSON string.
--- A Lua error is raised for syntax errors.
---@param tbl table lua table to encode
---@param options table table with encode options
---@return string encoded json
function json.encode(tbl, options) end

---@module label

---
--- Gets the text from a label component
---@param url string | hash | url the label to get the text from
---@return string the label text
function label.get_text(url) end

---
--- Sets the text of a label component
---  This method uses the message passing that means the value will be set after dispatch messages step.
--- More information is available in the Application Lifecycle manual.
---@param url string | hash | url the label that should have a constant set
---@param text string the text
function label.set_text(url, text) end

---@module liveupdate
---@field LIVEUPDATE_BUNDLED_RESOURCE_MISMATCH number Mismatch between between expected bundled resources and actual bundled resources. The manifest expects a resource to be in the bundle, but it was not found in the bundle. This is typically the case when a non-excluded resource was modified between publishing the bundle and publishing the manifest.
---@field LIVEUPDATE_ENGINE_VERSION_MISMATCH number Mismatch between running engine version and engine versions supported by manifest.
---@field LIVEUPDATE_FORMAT_ERROR number Failed to parse manifest data buffer. The manifest was probably produced by a different engine version.
---@field LIVEUPDATE_INVAL number Argument was invalid
---@field LIVEUPDATE_INVALID_HEADER number The handled resource is invalid.
---@field LIVEUPDATE_INVALID_RESOURCE number The header of the resource is invalid.
---@field LIVEUPDATE_IO_ERROR number I/O operation failed
---@field LIVEUPDATE_MEM_ERROR number Memory wasn't allocated
---@field LIVEUPDATE_OK number LIVEUPDATE_OK
---@field LIVEUPDATE_SCHEME_MISMATCH number Mismatch between scheme used to load resources. Resources are loaded with a different scheme than from manifest, for example over HTTP or directly from file. This is typically the case when running the game directly from the editor instead of from a bundle.
---@field LIVEUPDATE_SIGNATURE_MISMATCH number Mismatch between manifest expected signature and actual signature.
---@field LIVEUPDATE_UNKNOWN number Unspecified error
---@field LIVEUPDATE_VERSION_MISMATCH number Mismatch between manifest expected version and actual version.


---
--- Adds a resource mount to the resource system.
--- The mounts are persisted between sessions.
--- After the mount succeeded, the resources are available to load. (i.e. no reboot required)
---@param name string Unique name of the mount
---@param uri string The uri of the mount, including the scheme. Currently supported schemes are 'zip' and 'archive'.
---@param priority number Priority of mount. Larger priority takes prescedence
---@param callback function Callback after the asynchronous request completed
---@return number The result of the request
function liveupdate.add_mount(name, uri, priority, callback) end

---
--- Return a reference to the Manifest that is currently loaded.
---@return number reference to the Manifest that is currently loaded
function liveupdate.get_current_manifest() end

---
--- Get an array of the current mounts
--- This can be used to determine if a new mount is needed or not
---@return table Array of mounts
function liveupdate.get_mounts() end

---
--- Is any liveupdate data mounted and currently in use?
--- This can be used to determine if a new manifest or zip file should be downloaded.
---@return boolean true if a liveupdate archive (any format) has been loaded
function liveupdate.is_using_liveupdate_data() end

---
--- Remove a mount the resource system.
--- The remaining mounts are persisted between sessions.
--- Removing a mount does not affect any loaded resources.
---@param name string Unique name of the mount
---@return number The result of the call
function liveupdate.remove_mount(name) end

---
--- Stores a zip file and uses it for live update content. The contents of the
--- zip file will be verified against the manifest to ensure file integrity.
--- It is possible to opt out of the resource verification using an option passed
--- to this function.
--- The path is stored in the (internal) live update location.
---@param path string the path to the original file on disc
---@param callback function(self, status) the callback function executed after the storage has completed
---@param options table [optional]optional table with extra parameters. Supported entries:
function liveupdate.store_archive(path, callback, options) end

---
--- Create a new manifest from a buffer. The created manifest is verified
--- by ensuring that the manifest was signed using the bundled public/private
--- key-pair during the bundle process and that the manifest supports the current
--- running engine version. Once the manifest is verified it is stored on device.
--- The next time the engine starts (or is rebooted) it will look for the stored
--- manifest before loading resources. Storing a new manifest allows the
--- developer to update the game, modify existing resources, or add new
--- resources to the game through LiveUpdate.
---@param manifest_buffer string the binary data that represents the manifest
---@param callback function(self, status) the callback function executed once the engine has attempted to store the manifest.
function liveupdate.store_manifest(manifest_buffer, callback) end

---
--- add a resource to the data archive and runtime index. The resource will be verified
--- internally before being added to the data archive.
---@param manifest_reference number The manifest to check against.
---@param data string The resource data that should be stored.
---@param hexdigest string The expected hash for the resource, retrieved through collectionproxy.missing_resources.
---@param callback function(self, hexdigest, status) The callback function that is executed once the engine has been attempted to store the resource.
function liveupdate.store_resource(manifest_reference, data, hexdigest, callback) end

---@module model

---
--- Cancels all animation on a model component.
---@param url string | hash | url the model for which to cancel the animation
function model.cancel(url) end

---
--- Gets the id of the game object that corresponds to a model skeleton bone.
--- The returned game object can be used for parenting and transform queries.
--- This function has complexity O(n), where n is the number of bones in the model skeleton.
--- Game objects corresponding to a model skeleton bone can not be individually deleted.
---@param url string | hash | url the model to query
---@param bone_id string | hash id of the corresponding bone
---@return hash id of the game object
function model.get_go(url, bone_id) end

---
--- Get the enabled state of a mesh
---@param url string | hash | url the model
---@param mesh_id string | hash | url the id of the mesh
---@return boolean true if the mesh is visible, false otherwise
function model.get_mesh_enabled(url, mesh_id) end

---
--- Plays an animation on a model component with specified playback
--- mode and parameters.
--- An optional completion callback function can be provided that will be called when
--- the animation has completed playing. If no function is provided,
--- a model_animation_done message is sent to the script that started the animation.
---  The callback is not called (or message sent) if the animation is
--- cancelled with model.cancel. The callback is called (or message sent) only for
--- animations that play with the following playback modes:
---@param url string | hash | url the model for which to play the animation
---@param anim_id string | hash id of the animation to play
---@param playback constant playback mode of the animation
---@param play_properties table [optional]optional table with properties Play properties table:
---@param complete_function function(self, message_id, message, sender) [optional]function to call when the animation has completed.
function model.play_anim(url, anim_id, playback, play_properties, complete_function) end

---
--- Enable or disable visibility of a mesh
---@param url string | hash | url the model
---@param mesh_id string | hash | url the id of the mesh
---@param enabled boolean true if the mesh should be visible, false if it should be hideen
function model.set_mesh_enabled(url, mesh_id, enabled) end

---@module msg

---
--- Post a message to a receiving URL. The most common case is to send messages
--- to a component. If the component part of the receiver is omitted, the message
--- is broadcast to all components in the game object.
--- The following receiver shorthands are available:
---@param receiver string | url | hash The receiver must be a string in URL-format, a URL object or a hashed string.
---@param message_id string | hash The id must be a string or a hashed string.
---@param message table | nil [optional]a lua table with message parameters to send.
function msg.post(receiver, message_id, message) end

---
--- creates a new URL from separate arguments
---@overload fun(): url This is equivalent to msg.url(nil) or msg.url("#"), which creates an url to the current script component.
---@overload fun(urlstring: string): url The format of the string must be [socket:][path][#fragment], which is similar to a HTTP URL. When addressing instances:
---@param socket string | hash [optional]socket of the URL
---@param path string | hash [optional]path of the URL
---@param fragment string | hash [optional]fragment of the URL
---@return url a new URL
function msg.url(socket, path, fragment) end

---@module particlefx
---@field EMITTER_STATE_POSTSPAWN number The emitter is not spawning any particles, but has particles that are still alive.
---@field EMITTER_STATE_PRESPAWN number The emitter will be in this state when it has been started but before spawning any particles. Normally the emitter is in this state for a short time, depending on if a start delay has been set for this emitter or not.
---@field EMITTER_STATE_SLEEPING number The emitter does not have any living particles and will not spawn any particles in this state.
---@field EMITTER_STATE_SPAWNING number The emitter is spawning particles.


---
--- Starts playing a particle FX component.
--- Particle FX started this way need to be manually stopped through particlefx.stop().
--- Which particle FX to play is identified by the URL.
---  A particle FX will continue to emit particles even if the game object the particle FX component belonged to is deleted. You can call particlefx.stop() to stop it from emitting more particles.
---@param url string | hash | url the particle fx that should start playing.
---@param emitter_state_function function(self, id, emitter, state) [optional]optional callback function that will be called when an emitter attached to this particlefx changes state.
function particlefx.play(url, emitter_state_function) end

---
--- Resets a shader constant for a particle FX component emitter.
--- The constant must be defined in the material assigned to the emitter.
--- Resetting a constant through this function implies that the value defined in the material will be used.
--- Which particle FX to reset a constant for is identified by the URL.
---@param url string | hash | url the particle FX that should have a constant reset
---@param emitter string | hash the id of the emitter
---@param constant string | hash the name of the constant
function particlefx.reset_constant(url, emitter, constant) end

---
--- Sets a shader constant for a particle FX component emitter.
--- The constant must be defined in the material assigned to the emitter.
--- Setting a constant through this function will override the value set for that constant in the material.
--- The value will be overridden until particlefx.reset_constant is called.
--- Which particle FX to set a constant for is identified by the URL.
---@param url string | hash | url the particle FX that should have a constant set
---@param emitter string | hash the id of the emitter
---@param constant string | hash the name of the constant
---@param value vector4 the value of the constant
function particlefx.set_constant(url, emitter, constant, value) end

---
--- Stops a particle FX component from playing.
--- Stopping a particle FX does not remove already spawned particles.
--- Which particle FX to stop is identified by the URL.
---@param url string | hash | url the particle fx that should stop playing
---@param options table Options when stopping the particle fx. Supported options:
function particlefx.stop(url, options) end

---@module physics
---@field JOINT_TYPE_FIXED number The following properties are available when connecting a joint of
---@field JOINT_TYPE_HINGE number The following properties are available when connecting a joint of
---@field JOINT_TYPE_SLIDER number The following properties are available when connecting a joint of
---@field JOINT_TYPE_SPRING number The following properties are available when connecting a joint of
---@field JOINT_TYPE_WELD number The following properties are available when connecting a joint of
---@field JOINT_TYPE_WHEEL number The following properties are available when connecting a joint of


---
--- Create a physics joint between two collision object components.
--- Note: Currently only supported in 2D physics.
---@param joint_type number the joint type
---@param collisionobject_a string | hash | url first collision object
---@param joint_id string | hash id of the joint
---@param position_a vector3 local position where to attach the joint on the first collision object
---@param collisionobject_b string | hash | url second collision object
---@param position_b vector3 local position where to attach the joint on the second collision object
---@param properties table [optional]optional joint specific properties table See each joint type for possible properties field. The one field that is accepted for all joint types is: -
function physics.create_joint(joint_type, collisionobject_a, joint_id, position_a, collisionobject_b, position_b, properties) end

---
--- Destroy an already physics joint. The joint has to be created before a
--- destroy can be issued.
--- Note: Currently only supported in 2D physics.
---@param collisionobject string | hash | url collision object where the joint exist
---@param joint_id string | hash id of the joint
function physics.destroy_joint(collisionobject, joint_id) end

---
--- Get the gravity in runtime. The gravity returned is not global, it will return
--- the gravity for the collection that the function is called from.
--- Note: For 2D physics the z component will always be zero.
---@return vector3 gravity vector of collection
function physics.get_gravity() end

---
--- Returns the group name of a collision object as a hash.
---@param url string | hash | url the collision object to return the group of.
---@return hash hash value of the group.
function physics.get_group(url) end

---
--- Get a table for properties for a connected joint. The joint has to be created before
--- properties can be retrieved.
--- Note: Currently only supported in 2D physics.
---@param collisionobject string | hash | url collision object where the joint exist
---@param joint_id string | hash id of the joint
---@return table properties table. See the joint types for what fields are available, the only field available for all types is:
function physics.get_joint_properties(collisionobject, joint_id) end

---
--- Get the reaction force for a joint. The joint has to be created before
--- the reaction force can be calculated.
--- Note: Currently only supported in 2D physics.
---@param collisionobject string | hash | url collision object where the joint exist
---@param joint_id string | hash id of the joint
---@return vector3 reaction force for the joint
function physics.get_joint_reaction_force(collisionobject, joint_id) end

---
--- Get the reaction torque for a joint. The joint has to be created before
--- the reaction torque can be calculated.
--- Note: Currently only supported in 2D physics.
---@param collisionobject string | hash | url collision object where the joint exist
---@param joint_id string | hash id of the joint
---@return number the reaction torque on bodyB in N*m.
function physics.get_joint_reaction_torque(collisionobject, joint_id) end

---
--- Returns true if the specified group is set in the mask of a collision
--- object, false otherwise.
---@param url string | hash | url the collision object to check the mask of.
---@param group string the name of the group to check for.
---@return boolean boolean value of the maskbit. 'true' if present, 'false' otherwise.
function physics.get_maskbit(url, group) end

---
--- Gets collision shape data from a collision object
---@param url string | hash | url the collision object.
---@param shape string | hash the name of the shape to get data for.
---@return table A table containing meta data about the physics shape
function physics.get_shape(url, shape) end

---
--- Ray casts are used to test for intersections against collision objects in the physics world.
--- Collision objects of types kinematic, dynamic and static are tested against. Trigger objects
--- do not intersect with ray casts.
--- Which collision objects to hit is filtered by their collision groups and can be configured
--- through groups.
---@param from vector3 the world position of the start of the ray
---@param to vector3 the world position of the end of the ray
---@param groups table a lua table containing the hashed groups for which to test collisions against
---@param options table a lua table containing options for the raycast.
---@return table | nil It returns a list. If missed it returns
function physics.raycast(from, to, groups, options) end

---
--- Ray casts are used to test for intersections against collision objects in the physics world.
--- Collision objects of types kinematic, dynamic and static are tested against. Trigger objects
--- do not intersect with ray casts.
--- Which collision objects to hit is filtered by their collision groups and can be configured
--- through groups.
--- The actual ray cast will be performed during the physics-update.
---@param from vector3 the world position of the start of the ray
---@param to vector3 the world position of the end of the ray
---@param groups table a lua table containing the hashed groups for which to test collisions against
---@param request_id number [optional]a number between [0,-255]. It will be sent back in the response for identification, 0 by default
function physics.raycast_async(from, to, groups, request_id) end

---
--- Set the gravity in runtime. The gravity change is not global, it will only affect
--- the collection that the function is called from.
--- Note: For 2D physics the z component of the gravity vector will be ignored.
---@param gravity vector3 the new gravity vector
function physics.set_gravity(gravity) end

---
--- Updates the group property of a collision object to the specified
--- string value. The group name should exist i.e. have been used in
--- a collision object in the editor.
---@param url string | hash | url the collision object affected.
---@param group string the new group name to be assigned.
function physics.set_group(url, group) end

---
--- Flips the collision shapes horizontally for a collision object
---@param url string | hash | url the collision object that should flip its shapes
---@param flip boolean
function physics.set_hflip(url, flip) end

---
--- Updates the properties for an already connected joint. The joint has to be created before
--- properties can be changed.
--- Note: Currently only supported in 2D physics.
---@param collisionobject string | hash | url collision object where the joint exist
---@param joint_id string | hash id of the joint
---@param properties table joint specific properties table Note: The
function physics.set_joint_properties(collisionobject, joint_id, properties) end

---
--- sets a physics world event listener. If a function is set, physics messages will no longer be sent.
---@param callback function(self, event, data) | nil A callback that receives information about all the physics interactions in this physics world.
function physics.set_listener(callback) end

---
--- Sets or clears the masking of a group (maskbit) in a collision object.
---@param url string | hash | url the collision object to change the mask of.
---@param group string the name of the group (maskbit) to modify in the mask.
---@param maskbit boolean boolean value of the new maskbit. 'true' to enable, 'false' to disable.
function physics.set_maskbit(url, group, maskbit) end

---
--- Sets collision shape data for a collision object. Please note that updating data in 3D
--- can be quite costly for box and capsules. Because of the physics engine, the cost
--- comes from having to recreate the shape objects when certain shapes needs to be updated.
---@param url string | hash | url the collision object.
---@param shape string | hash the name of the shape to get data for.
---@param table table the shape data to update the shape with. See
function physics.set_shape(url, shape, table) end

---
--- Flips the collision shapes vertically for a collision object
---@param url string | hash | url the collision object that should flip its shapes
---@param flip boolean
function physics.set_vflip(url, flip) end

---
--- The function recalculates the density of each shape based on the total area of all shapes and the specified mass, then updates the mass of the body accordingly.
--- Note: Currently only supported in 2D physics.
---@param collisionobject string | hash | url the collision object whose mass needs to be updated.
---@param mass number the new mass value to set for the collision object.
function physics.update_mass(collisionobject, mass) end

---
--- Collision objects tend to fall asleep when inactive for a small period of time for
--- efficiency reasons. This function wakes them up.
---@param url string | hash | url the collision object to wake.
function physics.wakeup(url) end

---@module profiler
---@field MODE_PAUSE number pause on current frame
---@field MODE_RECORD number start recording
---@field MODE_RUN number continously show latest frame
---@field MODE_SHOW_PEAK_FRAME number pause at peak frame
---@field VIEW_MODE_FULL number show full profiler ui
---@field VIEW_MODE_MINIMIZED number show mimimal profiler ui


---
--- Creates and shows or hides and destroys the on-sceen profiler ui
--- The profiler is a real-time tool that shows the numbers of milliseconds spent
--- in each scope per frame as well as counters. The profiler is very useful for
--- tracking down performance and resource problems.
---@param enabled boolean true to enable, false to disable
function profiler.enable_ui(enabled) end

---
--- Get the percent of CPU usage by the application, as reported by the OS.
---  This function is not available on  HTML5.
--- For some platforms ( Android,  Linux and  Windows), this information is only available
--- by default in the debug version of the engine. It can be enabled in release version as well
--- by checking track_cpu under profiler in the game.project file.
--- (This means that the engine will sample the CPU usage in intervalls during execution even in release mode.)
---@return number of CPU used by the application
function profiler.get_cpu_usage() end

---
--- Get the amount of memory used (resident/working set) by the application in bytes, as reported by the OS.
---  This function is not available on  HTML5.
--- The values are gathered from internal OS functions which correspond to the following;
---@return number used by the application
function profiler.get_memory_usage() end

---
--- Send a text to the profiler
---@param text string the string to send to the profiler
function profiler.log_text(text) end

---
--- Get the number of recorded frames in the on-screen profiler ui recording buffer
---@return number the number of recorded frames, zero if on-screen profiler is disabled
function profiler.recorded_frame_count() end

---
--- Starts a profile scope.
---@param name string The name of the scope
function profiler.scope_begin(name) end

---
--- End the current profile scope.
function profiler.scope_end() end

---
--- Set the on-screen profile mode - run, pause, record or show peak frame
---@param mode constant the mode to set the ui profiler in
function profiler.set_ui_mode(mode) end

---
--- Set the on-screen profile view mode - minimized or expanded
---@param mode constant the view mode to set the ui profiler in
function profiler.set_ui_view_mode(mode) end

---
--- Shows or hides the time the engine waits for vsync in the on-screen profiler
--- Each frame the engine waits for vsync and depending on your vsync settings and how much time
--- your game logic takes this time can dwarf the time in the game logic making it hard to
--- see details in the on-screen profiler graph and lists.
--- Also, by hiding this the FPS times in the header show the time spent each time excuding the
--- time spent waiting for vsync. This shows you how long time your game is spending actively
--- working each frame.
--- This setting also effects the display of recorded frames but does not affect the actual
--- recorded frames so it is possible to toggle this on and off when viewing recorded frames.
--- By default the vsync wait times is displayed in the profiler.
---@param visible boolean true to include it in the display, false to hide it.
function profiler.set_ui_vsync_wait_visible(visible) end

---
--- Pauses and displays a frame from the recording buffer in the on-screen profiler ui
--- The frame to show can either be an absolute frame or a relative frame to the current frame.
---@param frame_index table a table where you specify one of the following parameters:
function profiler.view_recorded_frame(frame_index) end

---@module render
---@field BLEND_CONSTANT_ALPHA number
---@field BLEND_CONSTANT_COLOR number
---@field BLEND_DST_ALPHA number
---@field BLEND_DST_COLOR number
---@field BLEND_ONE number
---@field BLEND_ONE_MINUS_CONSTANT_ALPHA number
---@field BLEND_ONE_MINUS_CONSTANT_COLOR number
---@field BLEND_ONE_MINUS_DST_ALPHA number
---@field BLEND_ONE_MINUS_DST_COLOR number
---@field BLEND_ONE_MINUS_SRC_ALPHA number
---@field BLEND_ONE_MINUS_SRC_COLOR number
---@field BLEND_SRC_ALPHA number
---@field BLEND_SRC_ALPHA_SATURATE number
---@field BLEND_SRC_COLOR number
---@field BLEND_ZERO number
---@field BUFFER_COLOR0_BIT number
---@field BUFFER_COLOR1_BIT number
---@field BUFFER_COLOR2_BIT number
---@field BUFFER_COLOR3_BIT number
---@field BUFFER_COLOR_BIT number
---@field BUFFER_DEPTH_BIT number
---@field BUFFER_STENCIL_BIT number
---@field COMPARE_FUNC_ALWAYS number
---@field COMPARE_FUNC_EQUAL number
---@field COMPARE_FUNC_GEQUAL number
---@field COMPARE_FUNC_GREATER number
---@field COMPARE_FUNC_LEQUAL number
---@field COMPARE_FUNC_LESS number
---@field COMPARE_FUNC_NEVER number
---@field COMPARE_FUNC_NOTEQUAL number
---@field FACE_BACK number
---@field FACE_FRONT number
---@field FACE_FRONT_AND_BACK number
---@field FILTER_LINEAR number
---@field FILTER_NEAREST number
---@field FORMAT_DEPTH number
---@field FORMAT_LUMINANCE number
---@field FORMAT_R16F number May be nil if the format isn't supported
---@field FORMAT_R32F number May be nil if the format isn't supported
---@field FORMAT_RG16F number May be nil if the format isn't supported
---@field FORMAT_RG32F number May be nil if the format isn't supported
---@field FORMAT_RGB number
---@field FORMAT_RGB16F number May be nil if the format isn't supported
---@field FORMAT_RGB32F number May be nil if the format isn't supported
---@field FORMAT_RGBA number
---@field FORMAT_RGBA16F number May be nil if the format isn't supported
---@field FORMAT_RGBA32F number May be nil if the format isn't supported
---@field FORMAT_STENCIL number
---@field FRUSTUM_PLANES_ALL number
---@field FRUSTUM_PLANES_SIDES number
---@field RENDER_TARGET_DEFAULT number
---@field STATE_BLEND number
---@field STATE_CULL_FACE number
---@field STATE_DEPTH_TEST number
---@field STATE_POLYGON_OFFSET_FILL number
---@field STATE_STENCIL_TEST number
---@field STENCIL_OP_DECR number
---@field STENCIL_OP_DECR_WRAP number
---@field STENCIL_OP_INCR number
---@field STENCIL_OP_INCR_WRAP number
---@field STENCIL_OP_INVERT number
---@field STENCIL_OP_KEEP number
---@field STENCIL_OP_REPLACE number
---@field STENCIL_OP_ZERO number
---@field WRAP_CLAMP_TO_BORDER number
---@field WRAP_CLAMP_TO_EDGE number
---@field WRAP_MIRRORED_REPEAT number
---@field WRAP_REPEAT number


---
--- Clear buffers in the currently enabled render target with specified value. If the render target has been created with multiple
--- color attachments, all buffers will be cleared with the same value.
---@param buffers table table with keys specifying which buffers to clear and values set to clear values. Available keys are:
function render.clear(buffers) end

---
--- Constant buffers are used to set shader program variables and are optionally passed to the render.draw() function.
--- The buffer's constant elements can be indexed like an ordinary Lua table, but you can't iterate over them with pairs() or ipairs().
---@return constant_buffer new constant buffer
function render.constant_buffer() end

---
--- Deletes a render target created by a render script.
--- You cannot delete a render target resource.
---@param render_target render_target render target to delete
function render.delete_render_target(render_target) end

---
--- If a material is currently enabled, disable it.
--- The name of the material must be specified in the ".render" resource set
--- in the "game.project" setting.
function render.disable_material() end

---
--- Disables a render state.
---@param state constant state to disable
function render.disable_state(state) end

---
--- Disables a texture that has previourly been enabled.
---@param binding number | string | hash texture binding, either by texture unit, string or hash that should be disabled
function render.disable_texture(binding) end

---
--- Draws all objects that match a specified predicate. An optional constant buffer can be
--- provided to override the default constants. If no constants buffer is provided, a default
--- system constants buffer is used containing constants as defined in materials and set through
--- go.set (or particlefx.set_constant) on visual components.
---@param predicate predicate predicate to draw for
---@param options table [optional]optional table with properties:
function render.draw(predicate, options) end

---
--- Draws all 3d debug graphics such as lines drawn with "draw_line" messages and physics visualization.
---@param options table [optional]optional table with properties:
function render.draw_debug3d(options) end

---
--- If another material was already enabled, it will be automatically disabled
--- and the specified material is used instead.
--- The name of the material must be specified in the ".render" resource set
--- in the "game.project" setting.
---@param material_id string | hash material id to enable
function render.enable_material(material_id) end

---
--- Enables a particular render state. The state will be enabled until disabled.
---@param state constant state to enable
function render.enable_state(state) end

---
--- Sets the specified texture handle for a render target attachment or a regular texture
--- that should be used for rendering. The texture can be bound to either a texture unit
--- or to a sampler name by a hash or a string.
--- A texture can be bound to multiple units and sampler names at the same time,
--- the actual binding will be applied to the shaders when a shader program is bound.
--- When mixing binding using both units and sampler names, you might end up in situations
--- where two different textures will be applied to the same bind location in the shader.
--- In this case, the texture set to the named sampler will take precedence over the unit.
--- Note that you can bind multiple sampler names to the same texture, in case you want to reuse
--- the same texture for differnt use-cases. It is however recommended that you use the same name
--- everywhere for the textures that should be shared across different materials.
---@param binding number | string | hash texture binding, either by texture unit, string or hash for the sampler name that the texture should be bound to
---@param handle_or_name texture | string | hash render target or texture handle that should be bound, or a named resource in the "Render Resource" table in the currently assigned .render file
---@param buffer_type constant [optional]optional buffer type from which to enable the texture. Note that this argument only applies to render targets. Defaults to
function render.enable_texture(binding, handle_or_name, buffer_type) end

---
--- Returns the logical window height that is set in the "game.project" settings.
--- Note that the actual window pixel size can change, either by device constraints
--- or user input.
---@return number specified window height
function render.get_height() end

---
--- Returns the specified buffer height from a render target.
---@param render_target render_target render target from which to retrieve the buffer height
---@param buffer_type constant which type of buffer to retrieve the height from
---@return number the height of the render target buffer texture
function render.get_render_target_height(render_target, buffer_type) end

---
--- Returns the specified buffer width from a render target.
---@param render_target render_target render target from which to retrieve the buffer width
---@param buffer_type constant which type of buffer to retrieve the width from
---@return number the width of the render target buffer texture
function render.get_render_target_width(render_target, buffer_type) end

---
--- Returns the logical window width that is set in the "game.project" settings.
--- Note that the actual window pixel size can change, either by device constraints
--- or user input.
---@return number specified window width (number)
function render.get_width() end

---
--- Returns the actual physical window height.
--- Note that this value might differ from the logical height that is set in the
--- "game.project" settings.
---@return number actual window height
function render.get_window_height() end

---
--- Returns the actual physical window width.
--- Note that this value might differ from the logical width that is set in the
--- "game.project" settings.
---@return number actual window width
function render.get_window_width() end

---
--- This function returns a new render predicate for objects with materials matching
--- the provided material tags. The provided tags are combined into a bit mask
--- for the predicate. If multiple tags are provided, the predicate matches materials
--- with all tags ANDed together.
--- The current limit to the number of tags that can be defined is 64.
---@param tags table table of tags that the predicate should match. The tags can be of either hash or string type
---@return predicate new predicate
function render.predicate(tags) end

---
--- Creates a new render target according to the supplied
--- specification table.
--- The table should contain keys specifying which buffers should be created
--- with what parameters. Each buffer key should have a table value consisting
--- of parameters. The following parameter keys are available:
---@param name string render target name
---@param parameters table table of buffer parameters, see the description for available keys and values
---@return render_target new render target
function render.render_target(name, parameters) end

---
--- Specifies the arithmetic used when computing pixel values that are written to the frame
--- buffer. In RGBA mode, pixels can be drawn using a function that blends the source RGBA
--- pixel values with the destination pixel values already in the frame buffer.
--- Blending is initially disabled.
--- source_factor specifies which method is used to scale the source color components.
--- destination_factor specifies which method is used to scale the destination color
--- components.
--- Source color components are referred to as (Rs,Gs,Bs,As).
--- Destination color components are referred to as (Rd,Gd,Bd,Ad).
--- The color specified by setting the blendcolor is referred to as (Rc,Gc,Bc,Ac).
--- The source scale factor is referred to as (sR,sG,sB,sA).
--- The destination scale factor is referred to as (dR,dG,dB,dA).
--- The color values have integer values between 0 and (kR,kG,kB,kA), where kc = 2mc - 1 and mc is the number of bitplanes for that color. I.e for 8 bit color depth, color values are between 0 and 255.
--- Available factor constants and corresponding scale factors:
---@param source_factor constant source factor
---@param destination_factor constant destination factor
function render.set_blend_func(source_factor, destination_factor) end

---
--- Specifies whether the individual color components in the frame buffer is enabled for writing (true) or disabled (false). For example, if blue is false, nothing is written to the blue component of any pixel in any of the color buffers, regardless of the drawing operation attempted. Note that writing are either enabled or disabled for entire color components, not the individual bits of a component.
--- The component masks are all initially true.
---@param red boolean red mask
---@param green boolean green mask
---@param blue boolean blue mask
---@param alpha boolean alpha mask
function render.set_color_mask(red, green, blue, alpha) end

---
--- Specifies whether front- or back-facing polygons can be culled
--- when polygon culling is enabled. Polygon culling is initially disabled.
--- If mode is render.FACE_FRONT_AND_BACK, no polygons are drawn, but other
--- primitives such as points and lines are drawn. The initial value for
--- face_type is render.FACE_BACK.
---@param face_type constant face type
function render.set_cull_face(face_type) end

---
--- Specifies the function that should be used to compare each incoming pixel
--- depth value with the value present in the depth buffer.
--- The comparison is performed only if depth testing is enabled and specifies
--- the conditions under which a pixel will be drawn.
--- Function constants:
---@param func constant depth test function, see the description for available values
function render.set_depth_func(func) end

---
--- Specifies whether the depth buffer is enabled for writing. The supplied mask governs
--- if depth buffer writing is enabled (true) or disabled (false).
--- The mask is initially true.
---@param depth boolean depth mask
function render.set_depth_mask(depth) end

---
--- Sets the scale and units used to calculate depth values.
--- If render.STATE_POLYGON_OFFSET_FILL is enabled, each fragment's depth value
--- is offset from its interpolated value (depending on the depth value of the
--- appropriate vertices). Polygon offset can be used when drawing decals, rendering
--- hidden-line images etc.
--- factor specifies a scale factor that is used to create a variable depth
--- offset for each polygon. The initial value is 0.
--- units is multiplied by an implementation-specific value to create a
--- constant depth offset. The initial value is 0.
--- The value of the offset is computed as factor × DZ + r × units
--- DZ is a measurement of the depth slope of the polygon which is the change in z (depth)
--- values divided by the change in either x or y coordinates, as you traverse a polygon.
--- The depth values are in window coordinates, clamped to the range [0, 1].
--- r is the smallest value that is guaranteed to produce a resolvable difference.
--- It's value is an implementation-specific constant.
--- The offset is added before the depth test is performed and before the
--- value is written into the depth buffer.
---@param factor number polygon offset factor
---@param units number polygon offset units
function render.set_polygon_offset(factor, units) end

---
--- Sets the projection matrix to use when rendering.
---@param matrix matrix4 projection matrix
function render.set_projection(matrix) end

---
--- Sets a render target. Subsequent draw operations will be to the
--- render target until it is replaced by a subsequent call to set_render_target.
--- This function supports render targets created by a render script, or a render target resource.
---@param render_target render_target render target to set. render.RENDER_TARGET_DEFAULT to set the default render target
---@param options table [optional]optional table with behaviour parameters
function render.set_render_target(render_target, options) end

---
--- Sets the render target size for a render target created from
--- either a render script, or from a render target resource.
---@param render_target render_target render target to set size for
---@param width number new render target width
---@param height number new render target height
function render.set_render_target_size(render_target, width, height) end

---
--- Stenciling is similar to depth-buffering as it enables and disables drawing on a
--- per-pixel basis. First, GL drawing primitives are drawn into the stencil planes.
--- Second, geometry and images are rendered but using the stencil planes to mask out
--- where to draw.
--- The stencil test discards a pixel based on the outcome of a comparison between the
--- reference value ref and the corresponding value in the stencil buffer.
--- func specifies the comparison function. See the table below for values.
--- The initial value is render.COMPARE_FUNC_ALWAYS.
--- ref specifies the reference value for the stencil test. The value is clamped to
--- the range [0, 2n-1], where n is the number of bitplanes in the stencil buffer.
--- The initial value is 0.
--- mask is ANDed with both the reference value and the stored stencil value when the test
--- is done. The initial value is all 1's.
--- Function constant:
---@param func constant stencil test function, see the description for available values
---@param ref number reference value for the stencil test
---@param mask number mask that is ANDed with both the reference value and the stored stencil value when the test is done
function render.set_stencil_func(func, ref, mask) end

---
--- The stencil mask controls the writing of individual bits in the stencil buffer.
--- The least significant n bits of the parameter mask, where n is the number of
--- bits in the stencil buffer, specify the mask.
--- Where a 1 bit appears in the mask, the corresponding
--- bit in the stencil buffer can be written. Where a 0 bit appears in the mask,
--- the corresponding bit in the stencil buffer is never written.
--- The mask is initially all 1's.
---@param mask number stencil mask
function render.set_stencil_mask(mask) end

---
--- The stencil test discards a pixel based on the outcome of a comparison between the
--- reference value ref and the corresponding value in the stencil buffer.
--- To control the test, call render.set_stencil_func.
--- This function takes three arguments that control what happens to the stored stencil
--- value while stenciling is enabled. If the stencil test fails, no change is made to the
--- pixel's color or depth buffers, and sfail specifies what happens to the stencil buffer
--- contents.
--- Operator constants:
---@param sfail constant action to take when the stencil test fails
---@param dpfail constant the stencil action when the stencil test passes
---@param dppass constant the stencil action when both the stencil test and the depth test pass, or when the stencil test passes and either there is no depth buffer or depth testing is not enabled
function render.set_stencil_op(sfail, dpfail, dppass) end

---
--- Sets the view matrix to use when rendering.
---@param matrix matrix4 view matrix to set
function render.set_view(matrix) end

---
--- Set the render viewport to the specified rectangle.
---@param x number left corner
---@param y number bottom corner
---@param width number viewport width
---@param height number viewport height
function render.set_viewport(x, y, width, height) end

---@module resource
---@field COMPRESSION_TYPE_BASIS_UASTC number BASIS_UASTC compression type
---@field COMPRESSION_TYPE_DEFAULT number COMPRESSION_TYPE_DEFAULT compression type
---@field TEXTURE_FORMAT_LUMINANCE number luminance type texture format
---@field TEXTURE_FORMAT_R16F number R16F type texture format
---@field TEXTURE_FORMAT_R32F number R32F type texture format
---@field TEXTURE_FORMAT_R_BC4 number R_BC4 type texture format
---@field TEXTURE_FORMAT_RG16F number RG16F type texture format
---@field TEXTURE_FORMAT_RG32F number RG32F type texture format
---@field TEXTURE_FORMAT_RG_BC5 number RG_BC5 type texture format
---@field TEXTURE_FORMAT_RGB number RGB type texture format
---@field TEXTURE_FORMAT_RGB16F number RGB16F type texture format
---@field TEXTURE_FORMAT_RGB32F number RGB32F type texture format
---@field TEXTURE_FORMAT_RGB_BC1 number RGB_BC1 type texture format
---@field TEXTURE_FORMAT_RGB_ETC1 number RGB_ETC1 type texture format
---@field TEXTURE_FORMAT_RGB_PVRTC_2BPPV1 number RGB_PVRTC_2BPPV1 type texture format
---@field TEXTURE_FORMAT_RGB_PVRTC_4BPPV1 number RGB_PVRTC_4BPPV1 type texture format
---@field TEXTURE_FORMAT_RGBA number RGBA type texture format
---@field TEXTURE_FORMAT_RGBA16F number RGBA16F type texture format
---@field TEXTURE_FORMAT_RGBA32F number RGBA32F type texture format
---@field TEXTURE_FORMAT_RGBA_ASTC_4x4 number RGBA_ASTC_4x4 type texture format
---@field TEXTURE_FORMAT_RGBA_BC3 number RGBA_BC3 type texture format
---@field TEXTURE_FORMAT_RGBA_BC7 number RGBA_BC7 type texture format
---@field TEXTURE_FORMAT_RGBA_ETC2 number RGBA_ETC2 type texture format
---@field TEXTURE_FORMAT_RGBA_PVRTC_2BPPV1 number RGBA_PVRTC_2BPPV1 type texture format
---@field TEXTURE_FORMAT_RGBA_PVRTC_4BPPV1 number RGBA_PVRTC_4BPPV1 type texture format
---@field TEXTURE_TYPE_2D number 2D texture type
---@field TEXTURE_TYPE_2D_ARRAY number 2D Array texture type
---@field TEXTURE_TYPE_CUBE_MAP number Cube map texture type


---
--- Constructor-like function with two purposes:
---@param path string [optional]optional resource path string to the resource
---@return hash a path hash to the binary version of the resource
function resource.atlas(path) end

---
--- Constructor-like function with two purposes:
---@param path string [optional]optional resource path string to the resource
---@return hash a path hash to the binary version of the resource
function resource.buffer(path) end

---
--- This function creates a new atlas resource that can be used in the same way as any atlas created during build time.
--- The path used for creating the atlas must be unique, trying to create a resource at a path that is already
--- registered will trigger an error. If the intention is to instead modify an existing atlas, use the resource.set_atlas
--- function. Also note that the path to the new atlas resource must have a '.texturesetc' extension,
--- meaning "/path/my_atlas" is not a valid path but "/path/my_atlas.texturesetc" is.
--- When creating the atlas, at least one geometry and one animation is required, and an error will be
--- raised if these requirements are not met. A reference to the resource will be held by the collection
--- that created the resource and will automatically be released when that collection is destroyed.
--- Note that releasing a resource essentially means decreasing the reference count of that resource,
--- and not necessarily that it will be deleted.
---@param path string The path to the resource.
---@param table table A table containing info about how to create the atlas. Supported entries:
---@return hash Returns the atlas resource path
function resource.create_atlas(path, table) end

---
--- This function creates a new buffer resource that can be used in the same way as any buffer created during build time.
--- The function requires a valid buffer created from either buffer.create or another pre-existing buffer resource.
--- By default, the new resource will take ownership of the buffer lua reference, meaning the buffer will not automatically be removed
--- when the lua reference to the buffer is garbage collected. This behaviour can be overruled by specifying 'transfer_ownership = false'
--- in the argument table. If the new buffer resource is created from a buffer object that is created by another resource,
--- the buffer object will be copied and the new resource will effectively own a copy of the buffer instead.
--- Note that the path to the new resource must have the '.bufferc' extension, "/path/my_buffer" is not a valid path but "/path/my_buffer.bufferc" is.
--- The path must also be unique, attempting to create a buffer with the same name as an existing resource will raise an error.
---@param path string The path to the resource.
---@param table table A table containing info about how to create the buffer. Supported entries:
---@return hash Returns the buffer resource path
function resource.create_buffer(path, table) end

---
--- Creates a new texture resource that can be used in the same way as any texture created during build time.
--- The path used for creating the texture must be unique, trying to create a resource at a path that is already
--- registered will trigger an error. If the intention is to instead modify an existing texture, use the resource.set_texture
--- function. Also note that the path to the new texture resource must have a '.texturec' extension,
--- meaning "/path/my_texture" is not a valid path but "/path/my_texture.texturec" is.
--- If the texture is created without a buffer, the pixel data will be blank.
---@param path string The path to the resource.
---@param table table A table containing info about how to create the texture. Supported entries:
---@param buffer buffer optional buffer of precreated pixel data
---@return hash The path to the resource.
function resource.create_texture(path, table, buffer) end

---
--- Creates a new texture resource that can be used in the same way as any texture created during build time.
--- The path used for creating the texture must be unique, trying to create a resource at a path that is already
--- registered will trigger an error. If the intention is to instead modify an existing texture, use the resource.set_texture
--- function. Also note that the path to the new texture resource must have a '.texturec' extension,
--- meaning "/path/my_texture" is not a valid path but "/path/my_texture.texturec" is.
--- If the texture is created without a buffer, the pixel data will be blank.
--- The difference between the async version and resource.create_texture is that the texture data will be uploaded
--- in a graphics worker thread. The function will return a resource immediately that contains a 1x1 blank texture which can be used
--- immediately after the function call. When the new texture has been uploaded, the initial blank texture will be deleted and replaced with the
--- new texture. Be careful when using the initial texture handle handle as it will not be valid after the upload has finished.
---@param path string The path to the resource.
---@param table table
---@param buffer buffer optional buffer of precreated pixel data
---@return hash The path to the resource.
function resource.create_texture_async(path, table, buffer) end

---
--- Constructor-like function with two purposes:
---@param path string [optional]optional resource path string to the resource
---@return hash a path hash to the binary version of the resource
function resource.font(path) end

---
--- Returns the atlas data for an atlas
---@param path hash | string The path to the atlas resource
---@return table A table with the following entries:
function resource.get_atlas(path) end

---
--- gets the buffer from a resource
---@param path hash | string The path to the resource
---@return buffer The resource buffer
function resource.get_buffer(path) end

---
--- Gets render target info from a render target resource path or a render target handle
---@param path hash | string | handle The path to the resource or a render target handle
---@return table A table containing info about the render target:
function resource.get_render_target_info(path) end

---
--- Gets the text metrics from a font
---@param url hash the font to get the (unscaled) metrics from
---@param text string text to measure
---@param options table [optional]A table containing parameters for the text. Supported entries:
---@return table a table with the following fields:
function resource.get_text_metrics(url, text, options) end

---
--- Gets texture info from a texture resource path or a texture handle
---@param path hash | string | handle The path to the resource or a texture handle
---@return table A table containing info about the texture:
function resource.get_texture_info(path) end

---
--- Loads the resource data for a specific resource.
---@param path string The path to the resource
---@return buffer Returns the buffer stored on disc
function resource.load(path) end

---
--- Constructor-like function with two purposes:
---@param path string [optional]optional resource path string to the resource
---@return hash a path hash to the binary version of the resource
function resource.material(path) end

---
--- Release a resource.
---  This is a potentially dangerous operation, releasing resources currently being used can cause unexpected behaviour.
---@param path hash | string The path to the resource.
function resource.release(path) end

---
--- Sets the resource data for a specific resource
---@param path string | hash The path to the resource
---@param buffer buffer The buffer of precreated data, suitable for the intended resource type
function resource.set(path, buffer) end

---
--- Sets the data for a specific atlas resource. Setting new atlas data is specified by passing in
--- a texture path for the backing texture of the atlas, a list of geometries and a list of animations
--- that map to the entries in the geometry list. The geometry entries are represented by three lists:
--- vertices, uvs and indices that together represent triangles that are used in other parts of the
--- engine to produce render objects from.
--- Vertex and uv coordinates for the geometries are expected to be
--- in pixel coordinates where 0,0 is the top left corner of the texture.
--- There is no automatic padding or margin support when setting custom data,
--- which could potentially cause filtering artifacts if used with a material sampler that has linear filtering.
--- If that is an issue, you need to calculate padding and margins manually before passing in the geometry data to
--- this function.
---@param path hash | string The path to the atlas resource
---@param table table A table containing info about the atlas. Supported entries:
function resource.set_atlas(path, table) end

---
--- Sets the buffer of a resource. By default, setting the resource buffer will either copy the data from the incoming buffer object
--- to the buffer stored in the destination resource, or make a new buffer object if the sizes between the source buffer and the destination buffer
--- stored in the resource differs. In some cases, e.g performance reasons, it might be beneficial to just set the buffer object on the resource without copying or cloning.
--- To achieve this, set the transfer_ownership flag to true in the argument table. Transferring ownership from a lua buffer to a resource with this function
--- works exactly the same as resource.create_buffer: the destination resource will take ownership of the buffer held by the lua reference, i.e the buffer will not automatically be removed
--- when the lua reference to the buffer is garbage collected.
--- Note: When setting a buffer with transfer_ownership = true, the currently bound buffer in the resource will be destroyed.
---@param path hash | string The path to the resource
---@param buffer buffer The resource buffer
---@param table table A table containing info about how to set the buffer. Supported entries:
function resource.set_buffer(path, buffer, table) end

---
--- Update internal sound resource (wavc/oggc) with new data
---@param path hash | string The path to the resource
---@param buffer string A lua string containing the binary sound data
function resource.set_sound(path, buffer) end

---
--- Sets the pixel data for a specific texture.
---@param path hash | string The path to the resource
---@param table table A table containing info about the texture. Supported entries:
---@param buffer buffer The buffer of precreated pixel data
function resource.set_texture(path, table, buffer) end

---
--- Constructor-like function with two purposes:
---@param path string [optional]optional resource path string to the resource
---@return hash a path hash to the binary version of the resource
function resource.texture(path) end

---
--- Constructor-like function with two purposes:
---@param path string [optional]optional resource path string to the resource
---@return hash a path hash to the binary version of the resource
function resource.tile_source(path) end

---@module sound

---
--- Get mixer group gain
---  Note that gain is in linear scale, between 0 and 1.
--- To get the dB value from the gain, use the formula 20 * log(gain).
--- Inversely, to find the linear value from a dB value, use the formula
--- 10db/20.
---@param group string | hash group name
---@return number gain in linear scale
function sound.get_group_gain(group) end

---
--- Get a mixer group name as a string.
---  This function is to be used for debugging and
--- development tooling only. The function does a reverse hash lookup, which does not
--- return a proper string value when the game is built in release mode.
---@param group string | hash group name
---@return string group name
function sound.get_group_name(group) end

---
--- Get a table of all mixer group names (hashes).
---@return table table of mixer group names
function sound.get_groups() end

---
--- Get peak value from mixer group.
---  Note that gain is in linear scale, between 0 and 1.
--- To get the dB value from the gain, use the formula 20 * log(gain).
--- Inversely, to find the linear value from a dB value, use the formula
--- 10db/20.
--- Also note that the returned value might be an approximation and in particular
--- the effective window might be larger than specified.
---@param group string | hash group name
---@param window number window length in seconds
---@return number peak value for left channel
function sound.get_peak(group, window) end

---
--- Get RMS (Root Mean Square) value from mixer group. This value is the
--- square root of the mean (average) value of the squared function of
--- the instantaneous values.
--- For instance: for a sinewave signal with a peak gain of -1.94 dB (0.8 linear),
--- the RMS is 0.8 × 1/sqrt(2) which is about 0.566.
---  Note the returned value might be an approximation and in particular
--- the effective window might be larger than specified.
---@param group string | hash group name
---@param window number window length in seconds
---@return number RMS value for left channel
function sound.get_rms(group, window) end

---
--- Checks if background music is playing, e.g. from iTunes.
---  On non mobile platforms,
--- this function always return false.
---  On Android you can only get a correct reading
--- of this state if your game is not playing any sounds itself. This is a limitation
--- in the Android SDK. If your game is playing any sounds, even with a gain of zero, this
--- function will return false.
--- The best time to call this function is:
---@return boolean
function sound.is_music_playing() end

---
--- Checks if a phone call is active. If there is an active phone call all
--- other sounds will be muted until the phone call is finished.
---  On non mobile platforms,
--- this function always return false.
---@return boolean
function sound.is_phone_call_active() end

---
--- Pause all active voices
---@param url string | hash | url the sound that should pause
---@param pause boolean true if the sound should pause
function sound.pause(url, pause) end

---
--- Make the sound component play its sound. Multiple voices are supported. The limit is set to 32 voices per sound component.
---  Note that gain is in linear scale, between 0 and 1.
--- To get the dB value from the gain, use the formula 20 * log(gain).
--- Inversely, to find the linear value from a dB value, use the formula
--- 10db/20.
---  A sound will continue to play even if the game object the sound component belonged to is deleted. You can call sound.stop() to stop the sound.
---@param url string | hash | url the sound that should play
---@param play_properties table [optional]
---@param complete_function function(self, message_id, message, sender) [optional]function to call when the sound has finished playing or stopped manually via
---@return number The identifier for the sound voice
function sound.play(url, play_properties, complete_function) end

---
--- Set gain on all active playing voices of a sound.
---  Note that gain is in linear scale, between 0 and 1.
--- To get the dB value from the gain, use the formula 20 * log(gain).
--- Inversely, to find the linear value from a dB value, use the formula
--- 10db/20.
---@param url string | hash | url the sound to set the gain of
---@param gain number [optional]sound gain between 0 and 1. The final gain of the sound will be a combination of this gain, the group gain and the master gain.
function sound.set_gain(url, gain) end

---
--- Set mixer group gain
---  Note that gain is in linear scale, between 0 and 1.
--- To get the dB value from the gain, use the formula 20 * log(gain).
--- Inversely, to find the linear value from a dB value, use the formula
--- 10db/20.
---@param group string | hash group name
---@param gain number gain in linear scale
function sound.set_group_gain(group, gain) end

---
--- Set panning on all active playing voices of a sound.
--- The valid range is from -1.0 to 1.0, representing -45 degrees left, to +45 degrees right.
---@param url string | hash | url the sound to set the panning value to
---@param pan number [optional]sound panning between -1.0 and 1.0
function sound.set_pan(url, pan) end

---
--- Stop playing all active voices or just one voice if play_id provided
---@param url string | hash | url the sound component that should stop
---@param stop_properties table [optional]
function sound.stop(url, stop_properties) end

---@module sprite

---
--- Play an animation on a sprite component from its tile set
--- An optional completion callback function can be provided that will be called when
--- the animation has completed playing. If no function is provided,
--- a animation_done message is sent to the script that started the animation.
---@param url string | hash | url the sprite that should play the animation
---@param id string | hash hashed id of the animation to play
---@param complete_function function(self, message_id, message, sender) [optional]function to call when the animation has completed.
---@param play_properties table [optional]optional table with properties:
function sprite.play_flipbook(url, id, complete_function, play_properties) end

---
--- Sets horizontal flipping of the provided sprite's animations.
--- The sprite is identified by its URL.
--- If the currently playing animation is flipped by default, flipping it again will make it appear like the original texture.
---@param url string | hash | url the sprite that should flip its animations
---@param flip boolean
function sprite.set_hflip(url, flip) end

---
--- Sets vertical flipping of the provided sprite's animations.
--- The sprite is identified by its URL.
--- If the currently playing animation is flipped by default, flipping it again will make it appear like the original texture.
---@param url string | hash | url the sprite that should flip its animations
---@param flip boolean
function sprite.set_vflip(url, flip) end

---@module sys
---@field NETWORK_CONNECTED number network connected through other, non cellular, connection
---@field NETWORK_CONNECTED_CELLULAR number network connected through mobile cellular
---@field NETWORK_DISCONNECTED number no network connection found
---@field REQUEST_STATUS_ERROR_IO_ERROR number an asyncronous request is unable to read the resource
---@field REQUEST_STATUS_ERROR_NOT_FOUND number an asyncronous request is unable to locate the resource
---@field REQUEST_STATUS_FINISHED number an asyncronous request has finished successfully


---
--- deserializes buffer into a lua table
---@param buffer string buffer to deserialize from
---@return table lua table with deserialized data
function sys.deserialize(buffer) end

---
--- Check if a path exists
--- Good for checking if a file exists before loading a large file
---@param path string path to check
---@return boolean
function sys.exists(path) end

---
--- Terminates the game application and reports the specified code to the OS.
---@param code number exit code to report to the OS, 0 means clean exit
function sys.exit(code) end

---
--- Returns a table with application information for the requested app.
---  On iOS, the app_string is an url scheme for the app that is queried. Your
--- game needs to list the schemes that are queried in an LSApplicationQueriesSchemes array
--- in a custom "Info.plist".
---  On Android, the app_string is the package identifier for the app.
---@param app_string string platform specific string with application package or query, see above for details.
---@return table table with application information in the following fields:
function sys.get_application_info(app_string) end

---
--- The path from which the application is run.
---@return string path to application executable
function sys.get_application_path() end

---
--- Get integer config value from the game.project configuration file with optional default value
---@param key string key to get value for. The syntax is SECTION.KEY
---@param default_value number [optional](optional) default value to return if the value does not exist
---@return number config value as an integer. default_value if the config key does not exist. 0 if no default value was supplied.
function sys.get_config_int(key, default_value) end

---
--- Get number config value from the game.project configuration file with optional default value
---@param key string key to get value for. The syntax is SECTION.KEY
---@param default_value number [optional](optional) default value to return if the value does not exist
---@return number config value as an number. default_value if the config key does not exist. 0 if no default value was supplied.
function sys.get_config_number(key, default_value) end

---
--- Get string config value from the game.project configuration file with optional default value
---@param key string key to get value for. The syntax is SECTION.KEY
---@param default_value string [optional](optional) default value to return if the value does not exist
---@return string config value as a string. default_value if the config key does not exist. nil if no default value was supplied.
function sys.get_config_string(key, default_value) end

---
--- Returns the current network connectivity status
--- on mobile platforms.
--- On desktop, this function always return sys.NETWORK_CONNECTED.
---@return constant network connectivity status:
function sys.get_connectivity() end

---
--- Returns a table with engine information.
---@return table table with engine information in the following fields:
function sys.get_engine_info() end

---
--- Create a path to the host device for unit testing
--- Useful for saving logs etc during development
---@param filename string file to read from
---@return string the path prefixed with the proper host mount
function sys.get_host_path(filename) end

---
--- Returns an array of tables with information on network interfaces.
---@return table an array of tables. Each table entry contain the following fields:
function sys.get_ifaddrs() end

---
--- The save-file path is operating system specific and is typically located under the user's home directory.
---@param application_id string user defined id of the application, which helps define the location of the save-file
---@param file_name string file-name to get path for
---@return string path to save-file
function sys.get_save_file(application_id, file_name) end

---
--- Returns a table with system information.
---@param options table [optional]optional options table - ignore_secure
---@return table table with system information in the following fields:
function sys.get_sys_info(options) end

---
--- If the file exists, it must have been created by sys.save to be loaded.
---@param filename string file to read from
---@return table lua table, which is empty if the file could not be found
function sys.load(filename) end

---
--- The sys.load_buffer function will first try to load the resource
--- from any of the mounted resource locations and return the data if
--- any matching entries found. If not, the path will be tried
--- as is from the primary disk on the device.
--- In order for the engine to include custom resources in the build process, you need
--- to specify them in the "custom_resources" key in your "game.project" settings file.
--- You can specify single resource files or directories. If a directory is included
--- in the resource list, all files and directories in that directory is recursively
--- included:
--- For example "main/data/,assets/level_data.json".
---@param path string the path to load the buffer from
---@return buffer the buffer with data
function sys.load_buffer(path) end

---
--- The sys.load_buffer function will first try to load the resource
--- from any of the mounted resource locations and return the data if
--- any matching entries found. If not, the path will be tried
--- as is from the primary disk on the device.
--- In order for the engine to include custom resources in the build process, you need
--- to specify them in the "custom_resources" key in your "game.project" settings file.
--- You can specify single resource files or directories. If a directory is included
--- in the resource list, all files and directories in that directory is recursively
--- included:
--- For example "main/data/,assets/level_data.json".
--- Note that issuing multiple requests of the same resource will yield
--- individual buffers per request. There is no implic caching of the buffers
--- based on request path.
---@param path string the path to load the buffer from
---@param status_callback function(self, request_id, result) A status callback that will be invoked when a request has been handled, or an error occured. The result is a table containing:
---@return handle a handle to the request
function sys.load_buffer_async(path, status_callback) end

---
--- Loads a custom resource. Specify the full filename of the resource that you want
--- to load. When loaded, the file data is returned as a string.
--- If loading fails, the function returns nil plus the error message.
--- In order for the engine to include custom resources in the build process, you need
--- to specify them in the "custom_resources" key in your "game.project" settings file.
--- You can specify single resource files or directories. If a directory is included
--- in the resource list, all files and directories in that directory is recursively
--- included:
--- For example "main/data/,assets/level_data.json".
---@param filename string resource to load, full path
---@return string | nil loaded data, or
function sys.load_resource(filename) end

---
--- Open URL in default application, typically a browser
---@param url string url to open
---@param attributes table [optional]table with attributes
---@return boolean a boolean indicating if the url could be opened or not
function sys.open_url(url, attributes) end

---
--- Reboots the game engine with a specified set of arguments.
--- Arguments will be translated into command line arguments. Calling reboot
--- function is equivalent to starting the engine with the same arguments.
--- On startup the engine reads configuration from "game.project" in the
--- project root.
---@param arg1 string argument 1
---@param arg2 string argument 2
---@param arg3 string argument 3
---@param arg4 string argument 4
---@param arg5 string argument 5
---@param arg6 string argument 6
function sys.reboot(arg1, arg2, arg3, arg4, arg5, arg6) end

---
--- The table can later be loaded by sys.load.
--- Use sys.get_save_file to obtain a valid location for the file.
--- Internally, this function uses a workspace buffer sized output file sized 512kb.
--- This size reflects the output file size which must not exceed this limit.
--- Additionally, the total number of rows that any one table may contain is limited to 65536
--- (i.e. a 16 bit range). When tables are used to represent arrays, the values of
--- keys are permitted to fall within a 32 bit range, supporting sparse arrays, however
--- the limit on the total number of rows remains in effect.
---@param filename string file to write to
---@param table table lua table to save
---@return boolean a boolean indicating if the table could be saved or not
function sys.save(filename, table) end

---
--- The buffer can later deserialized by sys.deserialize.
--- This method has all the same limitations as sys.save.
---@param table table lua table to serialize
---@return string serialized data buffer
function sys.serialize(table) end

---
--- Sets the host that is used to check for network connectivity against.
---@param host string hostname to check against
function sys.set_connectivity_host(host) end

---
--- Set the Lua error handler function.
--- The error handler is a function which is called whenever a lua runtime error occurs.
---@param error_handler function(source, message, traceback) the function to be called on error
function sys.set_error_handler(error_handler) end

---
--- Set game update-frequency (frame cap). This option is equivalent to display.update_frequency in
--- the "game.project" settings but set in run-time. If Vsync checked in "game.project", the rate will
--- be clamped to a swap interval that matches any detected main monitor refresh rate. If Vsync is
--- unchecked the engine will try to respect the rate in software using timers. There is no
--- guarantee that the frame cap will be achieved depending on platform specifics and hardware settings.
---@param frequency number target frequency. 60 for 60 fps
function sys.set_update_frequency(frequency) end

---
--- Set the vsync swap interval. The interval with which to swap the front and back buffers
--- in sync with vertical blanks (v-blank), the hardware event where the screen image is updated
--- with data from the front buffer. A value of 1 swaps the buffers at every v-blank, a value of
--- 2 swaps the buffers every other v-blank and so on. A value of 0 disables waiting for v-blank
--- before swapping the buffers. Default value is 1.
--- When setting the swap interval to 0 and having vsync disabled in
--- "game.project", the engine will try to respect the set frame cap value from
--- "game.project" in software instead.
--- This setting may be overridden by driver settings.
---@param swap_interval number target swap interval.
function sys.set_vsync_swap_interval(swap_interval) end

---@module tilemap
---@field H_FLIP number flip tile horizontally
---@field ROTATE_180 number rotate tile 180 degrees clockwise
---@field ROTATE_270 number rotate tile 270 degrees clockwise
---@field ROTATE_90 number rotate tile 90 degrees clockwise
---@field V_FLIP number flip tile vertically


---
--- Get the bounds for a tile map. This function returns multiple values:
--- The lower left corner index x and y coordinates (1-indexed),
--- the tile map width and the tile map height.
--- The resulting values take all tile map layers into account, meaning that
--- the bounds are calculated as if all layers were collapsed into one.
---@param url string | hash | url the tile map
---@return number x coordinate of the bottom left corner
function tilemap.get_bounds(url) end

---
--- Get the tile set at the specified position in the tilemap.
--- The position is identified by the tile index starting at origin
--- with index 1, 1. (see tilemap.set_tile())
--- Which tile map and layer to query is identified by the URL and the
--- layer name parameters.
---@param url string | hash | url the tile map
---@param layer string | hash name of the layer for the tile
---@param x number x-coordinate of the tile
---@param y number y-coordinate of the tile
---@return number index of the tile
function tilemap.get_tile(url, layer, x, y) end

---
--- Replace a tile in a tile map with a new tile.
--- The coordinates of the tiles are indexed so that the "first" tile just
--- above and to the right of origin has coordinates 1,1.
--- Tiles to the left of and below origin are indexed 0, -1, -2 and so forth.
---@param url string | hash | url the tile map
---@param layer string | hash name of the layer for the tile
---@param x number x-coordinate of the tile
---@param y number y-coordinate of the tile
---@param tile number index of new tile to set. 0 resets the cell
---@param transform_bitmask number [optional]optional flip and/or rotation should be applied to the tile
function tilemap.set_tile(url, layer, x, y, tile, transform_bitmask) end

---
--- Sets the visibility of the tilemap layer
---@param url string | hash | url the tile map
---@param layer string | hash name of the layer for the tile
---@param visible boolean should the layer be visible
function tilemap.set_visible(url, layer, visible) end

---@module timer
---@field INVALID_TIMER_HANDLE number Indicates an invalid timer handle


---
--- You may cancel a timer from inside a timer callback.
--- Cancelling a timer that is already executed or cancelled is safe.
---@param handle hash the timer handle returned by timer.delay()
---@return boolean if the timer was active, false if the timer is already cancelled / complete
function timer.cancel(handle) end

---
--- Adds a timer and returns a unique handle.
--- You may create more timers from inside a timer callback.
--- Using a delay of 0 will result in a timer that triggers at the next frame just before
--- script update functions.
--- If you want a timer that triggers on each frame, set delay to 0.0f and repeat to true.
--- Timers created within a script will automatically die when the script is deleted.
---@param delay number time interval in seconds
---@param repeat_ boolean true = repeat timer until cancel, false = one-shot timer
---@param callback function(self, handle, time_elapsed) timer callback function
---@return hash identifier for the create timer, returns timer.INVALID_TIMER_HANDLE if the timer can not be created
function timer.delay(delay, repeat_, callback) end

---
--- Get information about timer.
---@param handle hash the timer handle returned by timer.delay()
---@return table | nil table or
function timer.get_info(handle) end

---
--- Manual triggering a callback for a timer.
---@param handle hash the timer handle returned by timer.delay()
---@return boolean if the timer was active, false if the timer is already cancelled / complete
function timer.trigger(handle) end

---@module vmath

---
--- Calculates the conjugate of a quaternion. The result is a
--- quaternion with the same magnitudes but with the sign of
--- the imaginary (vector) parts changed:
--- q* = [w, -v]
---@param q1 quaternion quaternion of which to calculate the conjugate
---@return quaternion the conjugate
function vmath.conj(q1) end

---
--- Given two linearly independent vectors P and Q, the cross product,
--- P × Q, is a vector that is perpendicular to both P and Q and
--- therefore normal to the plane containing them.
--- If the two vectors have the same direction (or have the exact
--- opposite direction from one another, i.e. are not linearly independent)
--- or if either one has zero length, then their cross product is zero.
---@param v1 vector3 first vector
---@param v2 vector3 second vector
---@return vector3 a new vector representing the cross product
function vmath.cross(v1, v2) end

---
--- The returned value is a scalar defined as:
--- P ⋅ Q = |P| |Q| cos θ
--- where θ is the angle between the vectors P and Q.
---@param v1 vector3 | vector4 first vector
---@param v2 vector3 | vector4 second vector
---@return number dot product
function vmath.dot(v1, v2) end

---
--- The resulting matrix is the inverse of the supplied matrix.
---  For ortho-normal matrices, e.g. regular object transformation,
--- use vmath.ortho_inv() instead.
--- The specialized inverse for ortho-normalized matrices is much faster
--- than the general inverse.
---@param m1 matrix4 matrix to invert
---@return matrix4 inverse of the supplied matrix
function vmath.inv(m1) end

---
--- Returns the length of the supplied vector or quaternion.
--- If you are comparing the lengths of vectors or quaternions, you should compare
--- the length squared instead as it is slightly more efficient to calculate
--- (it eliminates a square root calculation).
---@param v vector3 | vector4 | quat value of which to calculate the length
---@return number length
function vmath.length(v) end

---
--- Returns the squared length of the supplied vector or quaternion.
---@param v vector3 | vector4 | quat value of which to calculate the squared length
---@return number squared length
function vmath.length_sqr(v) end

---
--- Linearly interpolate between two values. Lerp is useful
--- to describe transitions from one value to another over time.
---  The function does not clamp t between 0 and 1.
---@overload fun(t: number, v1: vector3 | vector4, v2: vector3 | vector4): vector3 | vector4 Linearly interpolate between two vectors. The function treats the vectors as positions and interpolates between the positions in a straight line. Lerp is useful to describe transitions from one place to another over time.  The function does not clamp t between 0 and 1.
---@overload fun(t: number, q1: quaternion, q2: quaternion): quaternion Linearly interpolate between two quaternions. Linear interpolation of rotations are only useful for small rotations. For interpolations of arbitrary rotations, vmath.slerp yields much better results.  The function does not clamp t between 0 and 1.
---@param t number interpolation parameter, 0-1
---@param n1 number number to lerp from
---@param n2 number number to lerp to
---@return number the lerped number
function vmath.lerp(t, n1, n2) end

---
--- Creates a new matrix with all components set to the
--- corresponding values from the supplied matrix. I.e.
--- the function creates a copy of the given matrix.
---@overload fun(): matrix4 The resulting identity matrix describes a transform with no translation or rotation.
---@param m1 matrix4 existing matrix
---@return matrix4 matrix which is a copy of the specified matrix
function vmath.matrix4(m1) end

---
--- The resulting matrix describes a rotation around the axis by the specified angle.
---@param v vector3 axis
---@param angle number angle in radians
---@return matrix4 matrix represented by axis and angle
function vmath.matrix4_axis_angle(v, angle) end

---
--- The resulting matrix describes the same rotation as the quaternion, but does not have any translation (also like the quaternion).
---@param q quaternion quaternion to create matrix from
---@return matrix4 matrix represented by quaternion
function vmath.matrix4_from_quat(q) end

---
--- Constructs a frustum matrix from the given values. The left, right,
--- top and bottom coordinates of the view cone are expressed as distances
--- from the center of the near clipping plane. The near and far coordinates
--- are expressed as distances from the tip of the view frustum cone.
---@param left number coordinate for left clipping plane
---@param right number coordinate for right clipping plane
---@param bottom number coordinate for bottom clipping plane
---@param top number coordinate for top clipping plane
---@param near number coordinate for near clipping plane
---@param far number coordinate for far clipping plane
---@return matrix4 matrix representing the frustum
function vmath.matrix4_frustum(left, right, bottom, top, near, far) end

---
--- The resulting matrix is created from the supplied look-at parameters.
--- This is useful for constructing a view matrix for a camera or
--- rendering in general.
---@param eye vector3 eye position
---@param look_at vector3 look-at position
---@param up vector3 up vector
---@return matrix4 look-at matrix
function vmath.matrix4_look_at(eye, look_at, up) end

---
--- Creates an orthographic projection matrix.
--- This is useful to construct a projection matrix for a camera or rendering in general.
---@param left number coordinate for left clipping plane
---@param right number coordinate for right clipping plane
---@param bottom number coordinate for bottom clipping plane
---@param top number coordinate for top clipping plane
---@param near number coordinate for near clipping plane
---@param far number coordinate for far clipping plane
---@return matrix4 orthographic projection matrix
function vmath.matrix4_orthographic(left, right, bottom, top, near, far) end

---
--- Creates a perspective projection matrix.
--- This is useful to construct a projection matrix for a camera or rendering in general.
---@param fov number angle of the full vertical field of view in radians
---@param aspect number aspect ratio
---@param near number coordinate for near clipping plane
---@param far number coordinate for far clipping plane
---@return matrix4 perspective projection matrix
function vmath.matrix4_perspective(fov, aspect, near, far) end

---
--- The resulting matrix describes a rotation around the x-axis
--- by the specified angle.
---@param angle number angle in radians around x-axis
---@return matrix4 matrix from rotation around x-axis
function vmath.matrix4_rotation_x(angle) end

---
--- The resulting matrix describes a rotation around the y-axis
--- by the specified angle.
---@param angle number angle in radians around y-axis
---@return matrix4 matrix from rotation around y-axis
function vmath.matrix4_rotation_y(angle) end

---
--- The resulting matrix describes a rotation around the z-axis
--- by the specified angle.
---@param angle number angle in radians around z-axis
---@return matrix4 matrix from rotation around z-axis
function vmath.matrix4_rotation_z(angle) end

---
--- The resulting matrix describes a translation of a point
--- in euclidean space.
---@param position vector3 | vector4 position vector to create matrix from
---@return matrix4 matrix from the supplied position vector
function vmath.matrix4_translation(position) end

---
--- Performs an element wise multiplication between two vectors of the same type
--- The returned value is a vector defined as (e.g. for a vector3):
--- v = vmath.mul_per_elem(a, b) = vmath.vector3(a.x * b.x, a.y * b.y, a.z * b.z)
---@param v1 vector3 | vector4 first vector
---@param v2 vector3 | vector4 second vector
---@return vector3 | vector4 multiplied vector
function vmath.mul_per_elem(v1, v2) end

---
--- Normalizes a vector, i.e. returns a new vector with the same
--- direction as the input vector, but with length 1.
---  The length of the vector must be above 0, otherwise a
--- division-by-zero will occur.
---@param v1 vector3 | vector4 | quat vector to normalize
---@return vector3 | vector4 | quat new normalized vector
function vmath.normalize(v1) end

---
--- The resulting matrix is the inverse of the supplied matrix.
--- The supplied matrix has to be an ortho-normal matrix, e.g.
--- describe a regular object transformation.
---  For matrices that are not ortho-normal
--- use the general inverse vmath.inv() instead.
---@param m1 matrix4 ortho-normalized matrix to invert
---@return matrix4 inverse of the supplied matrix
function vmath.ortho_inv(m1) end

---
--- Calculates the extent the projection of the first vector onto the second.
--- The returned value is a scalar p defined as:
--- p = |P| cos θ / |Q|
--- where θ is the angle between the vectors P and Q.
---@param v1 vector3 vector to be projected on the second
---@param v2 vector3 vector onto which the first will be projected, must not have zero length
---@return number the projected extent of the first vector onto the second
function vmath.project(v1, v2) end

---
--- Creates a new quaternion with the components set
--- according to the supplied parameter values.
---@overload fun(): quaternion Creates a new identity quaternion. The identity quaternion is equal to: vmath.quat(0, 0, 0, 1)
---@overload fun(q1: quaternion): quaternion Creates a new quaternion with all components set to the corresponding values from the supplied quaternion. I.e. This function creates a copy of the given quaternion.
---@param x number x coordinate
---@param y number y coordinate
---@param z number z coordinate
---@param w number w coordinate
---@return quaternion new quaternion
function vmath.quat(x, y, z, w) end

---
--- The resulting quaternion describes a rotation of angle
--- radians around the axis described by the unit vector v.
---@param v vector3 axis
---@param angle number angle
---@return quaternion quaternion representing the axis-angle rotation
function vmath.quat_axis_angle(v, angle) end

---
--- The resulting quaternion describes the rotation from the
--- identity quaternion (no rotation) to the coordinate system
--- as described by the given x, y and z base unit vectors.
---@param x vector3 x base vector
---@param y vector3 y base vector
---@param z vector3 z base vector
---@return quaternion quaternion representing the rotation of the specified base vectors
function vmath.quat_basis(x, y, z) end

---
--- The resulting quaternion describes the rotation that,
--- if applied to the first vector, would rotate the first
--- vector to the second. The two vectors must be unit
--- vectors (of length 1).
---  The result is undefined if the two vectors point in opposite directions
---@param v1 vector3 first unit vector, before rotation
---@param v2 vector3 second unit vector, after rotation
---@return quaternion quaternion representing the rotation from first to second vector
function vmath.quat_from_to(v1, v2) end

---
--- The resulting quaternion describes a rotation of angle
--- radians around the x-axis.
---@param angle number angle in radians around x-axis
---@return quaternion quaternion representing the rotation around the x-axis
function vmath.quat_rotation_x(angle) end

---
--- The resulting quaternion describes a rotation of angle
--- radians around the y-axis.
---@param angle number angle in radians around y-axis
---@return quaternion quaternion representing the rotation around the y-axis
function vmath.quat_rotation_y(angle) end

---
--- The resulting quaternion describes a rotation of angle
--- radians around the z-axis.
---@param angle number angle in radians around z-axis
---@return quaternion quaternion representing the rotation around the z-axis
function vmath.quat_rotation_z(angle) end

---
--- Returns a new vector from the supplied vector that is
--- rotated by the rotation described by the supplied
--- quaternion.
---@param q quaternion quaternion
---@param v1 vector3 vector to rotate
---@return vector3 the rotated vector
function vmath.rotate(q, v1) end

---
--- Slerp travels the torque-minimal path maintaining constant
--- velocity, which means it travels along the straightest path along
--- the rounded surface of a sphere. Slerp is useful for interpolation
--- of rotations.
--- Slerp travels the torque-minimal path, which means it travels
--- along the straightest path the rounded surface of a sphere.
---  The function does not clamp t between 0 and 1.
---@overload fun(t: number, v1: vector3 | vector4, v2: vector3 | vector4): vector3 | vector4 Spherically interpolates between two vectors. The difference to lerp is that slerp treats the vectors as directions instead of positions in space. The direction of the returned vector is interpolated by the angle and the magnitude is interpolated between the magnitudes of the from and to vectors.  Slerp is computationally more expensive than lerp. The function does not clamp t between 0 and 1.
---@param t number interpolation parameter, 0-1
---@param q1 quaternion quaternion to slerp from
---@param q2 quaternion quaternion to slerp to
---@return quaternion the slerped quaternion
function vmath.slerp(t, q1, q2) end

---
--- Creates a vector of arbitrary size. The vector is initialized
--- with numeric values from a table.
---  The table values are converted to floating point
--- values. If a value cannot be converted, a 0 is stored in that
--- value position in the vector.
---@param t table table of numbers
---@return vector new vector
function vmath.vector(t) end

---
--- Creates a new vector with the components set to the
--- supplied values.
---@overload fun(): vector3 Creates a new zero vector with all components set to 0.
---@overload fun(n: number): vector3 Creates a new vector with all components set to the supplied scalar value.
---@overload fun(v1: vector3): vector3 Creates a new vector with all components set to the corresponding values from the supplied vector. I.e. This function creates a copy of the given vector.
---@param x number x coordinate
---@param y number y coordinate
---@param z number z coordinate
---@return vector3 new vector
function vmath.vector3(x, y, z) end

---
--- Creates a new vector with the components set to the
--- supplied values.
---@overload fun(): vector4 Creates a new zero vector with all components set to 0.
---@overload fun(n: number): vector4 Creates a new vector with all components set to the supplied scalar value.
---@overload fun(v1: vector4): vector4 Creates a new vector with all components set to the corresponding values from the supplied vector. I.e. This function creates a copy of the given vector.
---@param x number x coordinate
---@param y number y coordinate
---@param z number z coordinate
---@param w number w coordinate
---@return vector4 new vector
function vmath.vector4(x, y, z, w) end

---@module window
---@field DIMMING_OFF number Dimming mode is used to control whether or not a mobile device should dim the screen after a period without user interaction.
---@field DIMMING_ON number Dimming mode is used to control whether or not a mobile device should dim the screen after a period without user interaction.
---@field DIMMING_UNKNOWN number Dimming mode is used to control whether or not a mobile device should dim the screen after a period without user interaction. This mode indicates that the dim mode can't be determined, or that the platform doesn't support dimming.
---@field WINDOW_EVENT_DEICONIFIED number
---@field WINDOW_EVENT_FOCUS_GAINED number This event is sent to a window event listener when the game window or app screen has gained focus. This event is also sent at game startup and the engine gives focus to the game.
---@field WINDOW_EVENT_FOCUS_LOST number This event is sent to a window event listener when the game window or app screen has lost focus.
---@field WINDOW_EVENT_ICONFIED number
---@field WINDOW_EVENT_RESIZED number This event is sent to a window event listener when the game window or app screen is resized. The new size is passed along in the data field to the event listener.


---
--- Returns the current dimming mode set on a mobile device.
--- The dimming mode specifies whether or not a mobile device should dim the screen after a period without user interaction.
--- On platforms that does not support dimming, window.DIMMING_UNKNOWN is always returned.
---@return constant The mode for screen dimming
function window.get_dim_mode() end

---
--- This returns the current lock state of the mouse cursor
---@return boolean The lock state
function window.get_mouse_lock() end

---
--- This returns the current window size (width and height).
---@return number The window width
function window.get_size() end

---
--- Sets the dimming mode on a mobile device.
--- The dimming mode specifies whether or not a mobile device should dim the screen after a period without user interaction. The dimming mode will only affect the mobile device while the game is in focus on the device, but not when the game is running in the background.
--- This function has no effect on platforms that does not support dimming.
---@param mode constant The mode for screen dimming
function window.set_dim_mode(mode) end

---
--- Sets a window event listener.
---@param callback function(self, event, data) | nil A callback which receives info about window events. Pass an empty function or
function window.set_listener(callback) end

---
--- Set the locking state for current mouse cursor on a PC platform.
--- This function locks or unlocks the mouse cursor to the center point of the window. While the cursor is locked,
--- mouse position updates will still be sent to the scripts as usual.
---@param flag boolean The lock state for the mouse cursor
function window.set_mouse_lock(flag) end

---@module zlib

---
--- A lua error is raised is on error
---@param buf string buffer to deflate
---@return string deflated buffer
function zlib.deflate(buf) end

---
--- A lua error is raised is on error
---@param buf string buffer to inflate
---@return string inflated buffer
function zlib.inflate(buf) end
