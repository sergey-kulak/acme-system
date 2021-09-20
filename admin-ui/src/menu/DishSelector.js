import { useEffect, useState } from "react";
import * as Icon from 'react-feather';
import { hasValidationError } from "../common/utils";
import dishService from "../dish/dishService";
import './DishSelector.css'

function DishSelector({ companyId, publicPointId, readOnly,
    value, onChange, field, form }) {

    const [dishes, setDishes] = useState([]);
    const [selDishState, setSelDishState] = useState({
        available: [],
        added: [],
        selected: []
    });
    value = field && field.value ? field.value : value;

    useEffect(() => {
        if (companyId && publicPointId) {
            dishService.findNames(companyId, publicPointId)
                .then(response => response.data)
                .then(setDishes)
        }
    }, [companyId, publicPointId]);

    useEffect(() => {
        if (dishes.length) {
            let selectedIds = value || [];
            setSelDishState({
                available: dishes.filter(dish => !selectedIds.includes(dish.id)),
                added: dishes.filter(dish => selectedIds.includes(dish.id)),
                selected: []
            });
        }
    }, [dishes, value]);

    function fireChange(newAdded) {
        let selectedIds = newAdded.map(item => item.id);
        if (field && field.onChange) {
            let event = {
                target: {
                    name: field.name,
                    value: selectedIds
                }
            };
            field.onChange(event);
        }
        if (onChange) {
            onChange(selectedIds);
        }
    }

    function itemClassName(dish) {
        return 'list-group-item list-group-item-action'
            + (selDishState.selected.includes(dish) ? ' list-group-item-dark' : '');
    }

    function sort(items) {
        items.sort((a, b) => {
            let fa = a.name.toLowerCase(),
                fb = b.name.toLowerCase();

            if (fa < fb) {
                return -1;
            }
            if (fa > fb) {
                return 1;
            }
            return 0;
        });

        return items;
    }

    function onClickAvailable(e, dish) {
        e.preventDefault();
        if (!readOnly) {
            let selected = selDishState.selected
            selected = selected.includes(dish) ?
                selected.filter(prItem => prItem !== dish) : [...selected, dish];
            setSelDishState(prev => ({
                ...prev,
                selected
            }));
        }
    }

    function onClickAdded(e, dish) {
        e.preventDefault();
        if (!readOnly) {
            let selected = selDishState.selected
            selected = selected.includes(dish) ?
                selected.filter(prItem => prItem !== dish) : [...selected, dish];
            setSelDishState(prev => ({
                ...prev,
                selected
            }));
        }
    }

    function add(e) {
        e.preventDefault();
        let toAddItems = selDishState.selected.filter(item => selDishState.available.includes(item));
        if (toAddItems.length) {
            let newAdded = [...selDishState.added, ...toAddItems];
            setSelDishState(prev => ({
                available: prev.available.filter(dish => !toAddItems.includes(dish)),
                added: sort(newAdded),
                selected: []
            }));
            fireChange(newAdded);
        }
    }

    function remove(e) {
        e.preventDefault();
        let toRemoveItems = selDishState.selected.filter(item => selDishState.added.includes(item));
        if (toRemoveItems.length) {
            let newAdded = selDishState.added.filter(dish => !toRemoveItems.includes(dish));
            setSelDishState(prev => ({
                available: sort([...prev.available, ...toRemoveItems]),
                added: newAdded,
                selected: []
            }));
            fireChange(newAdded);
        }
    }

    return (
        <div className="dish-selector">
            <div className="dish-list">
                <div className="mb-1">Available dishes:</div>
                <div className="list-group">
                    {
                        selDishState.available.map(dish =>
                            <a key={dish.id} href="#avItem" className={itemClassName(dish)}
                                onClick={e => onClickAvailable(e, dish)}>{dish.name}</a>
                        )
                    }
                </div>
            </div>
            {!readOnly && <div className="buttons-panel">
                <a href="#add" className="btn btn-light cmt-btn"
                    onClick={add}>
                    <Icon.ChevronsRight className="filter-icon" />
                </a>
                <a href="#remove" className="btn btn-light cmt-btn"
                    onClick={remove}>
                    <Icon.ChevronsLeft className="filter-icon" />
                </a>
            </div>}
            <div className="dish-list">
                <div className="mb-1">Added dishes:</div>
                <div className="list-group">
                    {
                        selDishState.added.map(dish =>
                            <a key={dish.id} href="#addedItem" className={itemClassName(dish)}
                                onClick={e => onClickAdded(e, dish)}>{dish.name}</a>
                        )
                    }
                </div>
                {
                    hasValidationError(form, field) &&
                    <small className="form-text text-danger">
                        <span >
                            {form.errors[field.name]}
                        </span >
                    </small >
                }
            </div>
        </div>
    )
}

export default DishSelector;