/*
 * Copyright (c) 2002-2017 "Neo Technology,"
 * Network Engine for Objects in Lund AB [http://neotechnology.com]
 *
 * This file is part of Neo4j.
 *
 * Neo4j is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.neo4j.kernel.impl.api.operations;

import org.neo4j.collection.primitive.PrimitiveIntIterator;
import org.neo4j.collection.primitive.PrimitiveIntSet;
import org.neo4j.collection.primitive.PrimitiveLongIterator;
import org.neo4j.cursor.Cursor;
import org.neo4j.kernel.api.exceptions.EntityNotFoundException;
import org.neo4j.kernel.api.exceptions.index.IndexNotFoundKernelException;
import org.neo4j.kernel.api.exceptions.schema.IndexBrokenKernelException;
import org.neo4j.kernel.api.schema_new.index.NewIndexDescriptor;
import org.neo4j.kernel.impl.api.KernelStatement;
import org.neo4j.kernel.impl.api.RelationshipVisitor;
import org.neo4j.storageengine.api.Direction;
import org.neo4j.storageengine.api.NodeItem;
import org.neo4j.storageengine.api.RelationshipItem;

public interface EntityReadOperations
{
    /**
     * @param labelId the label id of the label that returned nodes are guaranteed to have
     * @return ids of all nodes that have the given label
     */
    PrimitiveLongIterator nodesGetForLabel( KernelStatement state, int labelId );

    /**
     * Returns an iterable with the matched nodes.
     *
     * @throws IndexNotFoundKernelException if no such index found.
     */
    PrimitiveLongIterator nodesGetFromIndexSeek( KernelStatement state, NewIndexDescriptor index, Object value )
            throws IndexNotFoundKernelException;

    /**
     * Returns an iterable with the matched nodes.
     *
     * @throws IndexNotFoundKernelException if no such index found.
     */
    PrimitiveLongIterator nodesGetFromIndexRangeSeekByNumber( KernelStatement state,
            NewIndexDescriptor index,
            Number lower,
            boolean includeLower,
            Number upper,
            boolean includeUpper )
            throws IndexNotFoundKernelException;

    /**
     * Returns an iterable with the matched nodes.
     *
     * @throws IndexNotFoundKernelException if no such index found.
     */
    PrimitiveLongIterator nodesGetFromIndexRangeSeekByString( KernelStatement state,
            NewIndexDescriptor index,
            String lower,
            boolean includeLower,
            String upper,
            boolean includeUpper )
            throws IndexNotFoundKernelException;

    /**
     * Returns an iterable with the matched nodes.
     *
     * @throws IndexNotFoundKernelException if no such index found.
     */
    PrimitiveLongIterator nodesGetFromIndexRangeSeekByPrefix( KernelStatement state,
            NewIndexDescriptor index,
            String prefix )
            throws IndexNotFoundKernelException;

    /**
     * Returns an iterable with the matched nodes.
     *
     * @throws IndexNotFoundKernelException if no such index found.
     */
    PrimitiveLongIterator nodesGetFromIndexScan( KernelStatement state, NewIndexDescriptor index )
            throws IndexNotFoundKernelException;

    /**
     * Returns an iterable with the matched nodes.
     *
     * @throws IndexNotFoundKernelException if no such index found.
     */
    PrimitiveLongIterator nodesGetFromIndexContainsScan( KernelStatement state, NewIndexDescriptor index, String term )
            throws IndexNotFoundKernelException;

    /**
     * Returns an iterable with the matched nodes.
     *
     * @throws IndexNotFoundKernelException if no such index found.
     */
    PrimitiveLongIterator nodesGetFromIndexEndsWithScan( KernelStatement state, NewIndexDescriptor index, String suffix )
            throws IndexNotFoundKernelException;

    /**
     * Returns an iterable with the matched node.
     *
     * @throws IndexNotFoundKernelException if no such index found.
     * @throws IndexBrokenKernelException   if we found an index that was corrupt or otherwise in a failed state.
     */
    long nodeGetFromUniqueIndexSeek( KernelStatement state, NewIndexDescriptor index, Object value )
            throws IndexNotFoundKernelException, IndexBrokenKernelException;

    long nodesCountIndexed( KernelStatement statement, NewIndexDescriptor index, long nodeId, Object value )
            throws IndexNotFoundKernelException, IndexBrokenKernelException;

    boolean graphHasProperty( KernelStatement state, int propertyKeyId );

    Object graphGetProperty( KernelStatement state, int propertyKeyId );

    /**
     * Return all property keys associated with a relationship.
     */
    PrimitiveIntIterator graphGetPropertyKeys( KernelStatement state );

    PrimitiveLongIterator nodesGetAll( KernelStatement state );

    PrimitiveLongIterator relationshipsGetAll( KernelStatement state );

    <EXCEPTION extends Exception> void relationshipVisit( KernelStatement statement, long relId,
            RelationshipVisitor<EXCEPTION> visitor ) throws EntityNotFoundException, EXCEPTION;

    Cursor<NodeItem> nodeCursorById( KernelStatement statement, long nodeId ) throws EntityNotFoundException;

    Cursor<RelationshipItem> relationshipCursorById( KernelStatement statement, long relId )
            throws EntityNotFoundException;

    Cursor<RelationshipItem> relationshipCursorGetAll( KernelStatement statement );

    long nodesGetCount( KernelStatement statement );

    long relationshipsGetCount( KernelStatement statement );

    boolean nodeExists( KernelStatement statement, long id );

    /**
     * Returns the set of types for relationships attached to this node.
     *
     * @param statement the current kernel statement
     * @param nodeItem the node
     * @return the set of types for relationships attached to this node.
     * @throws IllegalStateException if no current node is selected
     */
    PrimitiveIntSet relationshipTypes( KernelStatement statement, NodeItem nodeItem );

    /**
     * Returns degree, e.g. number of relationships for this node.
     *
     * @param statement the current kernel statement
     * @param nodeItem the node
     * @param direction {@link Direction} filter when counting relationships, e.g. only
     * {@link Direction#OUTGOING outgoing} or {@link Direction#INCOMING incoming}.
     * @return degree of relationships in the given direction.
     */
    int degree( KernelStatement statement, NodeItem nodeItem, Direction direction );

    /**
     * Returns degree, e.g. number of relationships for this node.
     *
     * @param statement the current kernel statement
     * @param nodeItem the node
     * @param direction {@link Direction} filter on when counting relationships, e.g. only
     * {@link Direction#OUTGOING outgoing} or {@link Direction#INCOMING incoming}.
     * @param relType relationship type id to filter when counting relationships.
     * @return degree of relationships in the given direction and relationship type.
     */
    int degree( KernelStatement statement, NodeItem nodeItem, Direction direction, int relType );
}
