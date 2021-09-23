import { useCallback, useEffect, useState } from "react";
import { connect } from "react-redux";
import { useLocation, useParams } from "react-router";
import fileService from "../common/fileService";
import { onAdd } from "../order/cartReducer";
import { onSuccess } from "../common/toastNotification";
import Dish from "./Dish";
import menuService from "./menuService";

function Menu({ auth, onAdd, onSuccess }) {
    const { categoryId } = useParams();
    const { state } = useLocation();
    const [categoryName, setCategoryName] = useState()
    const [dishes, setDishes] = useState([]);
    const [imageUrls, setImageUrls] = useState({});

    const loadData = useCallback(() => {
        return menuService.findDishes(categoryId)
            .then(response => response.data)
    }, [categoryId]);

    const loadImages = useCallback(data => {
        if (!data.length) {
            return Promise.resolve(data);
        }
        let request = {
            companyId: auth.user.cmpid,
            publicPointId: auth.user.ppid,
            action: 'DOWNLOAD',
            imageKeys: data.map(dish => dish.primaryImage)
        }

        return fileService.getDishImageUrls(request)
            .then(response => setImageUrls(response.data))
            .then(() => data)
    }, [auth]);

    useEffect(() => {
        if (categoryId) {
            loadData()
                .then(loadImages)
                .then(setDishes)
        }
    }, [categoryId, loadData, loadImages]);

    useEffect(() => {
        if (!state || !state.categoryName) {
            menuService.getCategories()
                .then(response => response.data)
                .then(categories => categories.filter(ctg => ctg.id === categoryId))
                .then(setCategoryName)
        } else {
            setCategoryName(state.categoryName);
        }
    }, [state, categoryId]);

    function onAddClick(dish) {
        onAdd({
            dishId: dish.id,
            quantity: 1
        });
        onSuccess('Added', { delay: 600 });
    }

    return (
        <div className="main-content">
            {categoryName && <div>
                <div className="main-content-title mb-2">
                    {categoryName}
                </div>
                <div className="main-content-body">
                    {
                        dishes.map(dish => <Dish dish={dish} key={dish.id}
                            onAddClick={onAddClick}
                            image={imageUrls[dish.primaryImage]}
                        />)
                    }
                </div>
            </div>}
        </div>
    );
}

const mapStateToProps = ({ auth }) => {
    return { auth };
};
export default connect(mapStateToProps, {
    onAdd, onSuccess
})(Menu);