import React, {useContext, useEffect, useState} from "react";
import Paper from '@mui/material/Paper';
import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableCell from '@mui/material/TableCell';
import TableContainer from '@mui/material/TableContainer';
import TableHead from '@mui/material/TableHead';
import TablePagination from '@mui/material/TablePagination';
import TableRow from '@mui/material/TableRow';
import Api from "../http/api";
import {Link} from "react-router-dom";
import {AuthContext} from "../App";
import {serverIsDeadHandler} from "./common/GlobalErrorHandlers";

const columns = [
    { id: 'name', label: 'Name', minWidth: 170 },
    { id: 'owner', label: 'Owner name', minWidth: 100 },
    {
        id: 'numParticipants',
        label: 'Number of participants',
        minWidth: 170,
        align: 'right',
    },
    {
        id: 'password',
        label: 'Has password',
        minWidth: 170,
        align: 'right',
    },
    {
        id: 'join',
        label: 'Join',
        minWidth: 100,
        align: 'right'
    },
];


const Lobbies = () => {
    const [page, setPage] = React.useState(0);
    const [rowsPerPage, setRowsPerPage] = React.useState(10);

    const handleChangePage = (event, newPage) => {
        setPage(newPage);
    };

    const handleChangeRowsPerPage = (event) => {
        setRowsPerPage(+event.target.value);
        setPage(0);
    };

    const [rows, setRows] = useState([]);

    const auth = useContext(AuthContext);

    useEffect(() => {
        const getData = () => new Promise(async (resolve, reject) => {
            try {
                const response = await Api.get('/lobby');
                const data = await response.json();
                if (data.statusCode === 401) {
                    auth.setIsLoginShown(true);
                    auth.loginCallback = () => {
                        auth.setIsLoginShown(false);
                        return getData();
                    }
                } else {
                    resolve(data);
                }
            } catch (e) {
                serverIsDeadHandler(e, auth);
            }
        });

        const loadRows = async () => {
            const data = await getData();
            setRows(data.map(r => {
                return {
                    id: r.id,
                    name: r.name,
                    owner: r.owner.name,
                    numParticipants: r.participants.length,
                    password: r.password ? 'yes' : 'no',
                    join: (
                        <Link to={`/lobby/${r.id}`}>Join</Link>
                    )
                }
            }));
        };
        if (!rows.length) loadRows();
    });

    return (
        <>
            <Paper sx={{width: '100%', overflow: 'hidden'}}>
                <TableContainer sx={{maxHeight: 440}}>
                    <Table stickyHeader aria-label="sticky table">
                        <TableHead>
                            <TableRow>
                                {columns.map((column) => (
                                    <TableCell
                                        key={column.id}
                                        align={column.align}
                                        style={{minWidth: column.minWidth}}
                                    >
                                        {column.label}
                                    </TableCell>
                                ))}
                            </TableRow>
                        </TableHead>
                        <TableBody>
                            {rows
                                .slice(page * rowsPerPage, page * rowsPerPage + rowsPerPage)
                                .map((row) => {
                                    return (
                                        <TableRow hover role="checkbox" tabIndex={-1} key={`lobby-${row.id}`}>
                                            {columns.map((column) => {
                                                const value = row[column.id];
                                                return (
                                                    <TableCell key={column.id} align={column.align}>
                                                        {column.format && typeof value === 'number'
                                                            ? column.format(value)
                                                            : value}
                                                    </TableCell>
                                                );
                                            })}
                                        </TableRow>
                                    );
                                })}
                        </TableBody>
                    </Table>
                </TableContainer>
                <TablePagination
                    rowsPerPageOptions={[10, 25, 100]}
                    component="div"
                    count={rows.length}
                    rowsPerPage={rowsPerPage}
                    page={page}
                    onPageChange={handleChangePage}
                    onRowsPerPageChange={handleChangeRowsPerPage}
                />
            </Paper>
        </>
    );
};

export default Lobbies;